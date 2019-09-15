package com.ronnnnn.zeroconfsample

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ronnnnn.zeroconfsample.databinding.ActivityGuestBinding

class GuestActivity : AppCompatActivity() {

    private var isServiceDiscoveryStarted: Boolean = false

    private val nsdManager: NsdManager by lazy {
        getSystemService(Context.NSD_SERVICE) as? NsdManager
            ?: throw IllegalStateException("can't get NsdManager")
    }
    private val serviceRecyclerAdapter: ServiceRecyclerAdapter by lazy {
        ServiceRecyclerAdapter()
    }

    private lateinit var binding: ActivityGuestBinding

    private val serviceListLiveData: MutableLiveData<List<NsdServiceInfo>> =
        MutableLiveData()

    private val discoveryListener: NsdManager.DiscoveryListener =
        object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(serviceType: String?) {
                "Start service discovery".showAsLog()
                isServiceDiscoveryStarted = true
            }

            override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
                "Start service discovery failed".showAsLog()
            }

            override fun onDiscoveryStopped(serviceType: String?) {
                "Stop service discovery".showAsLog()
                isServiceDiscoveryStarted = false
            }

            override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
                "Stop service discovery failed".showAsLog()
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
                serviceInfo ?: return
                val serviceList = (serviceListLiveData.value ?: emptyList()).toMutableList()
                val service = serviceList.find { it.serviceType == serviceInfo.serviceType }
                if (service == null) {
                    serviceList.add(serviceInfo)
                    serviceListLiveData.postValue(serviceList)
                }
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
                serviceInfo ?: return
                val serviceList = (serviceListLiveData.value ?: emptyList()).toMutableList()
                val index = serviceList.indexOfFirst { it.serviceType == serviceInfo.serviceType }
                if (index != -1) {
                    serviceList.removeAt(index)
                    serviceListLiveData.postValue(serviceList)
                }
            }
        }

    private val resolveListener: NsdManager.ResolveListener =
        object : NsdManager.ResolveListener {
            override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                "Resolve service".showAsLog()
                serviceInfo?.toString()?.showAsLog()
            }

            override fun onResolveFailed(
                serviceInfo: NsdServiceInfo?,
                errorCode: Int
            ) {
                "Resolve service failed: $errorCode".showAsLog()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityGuestBinding>(
            this,
            R.layout.activity_guest
        ).apply {
            serviceRecyclerView.adapter = serviceRecyclerAdapter.apply {
                listener = object : ServiceRecyclerAdapter.OnItemClickListener {
                    override fun onItemClicked(position: Int) {
                        val serviceList = serviceListLiveData.value ?: return
                        resolveService(serviceList[position])
                    }
                }
            }
            serviceRecyclerView.layoutManager = LinearLayoutManager(this@GuestActivity)
        }

        serviceListLiveData.observe(this, Observer { serviceList ->
            serviceRecyclerAdapter.submitList(serviceList.map { it.toString() }.toList())
        })

        discoverServices()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceDiscoveryStarted) nsdManager.stopServiceDiscovery(discoveryListener)
    }

    private fun discoverServices() {
        nsdManager.discoverServices(
            SEARCH_SERVICE_TYPE,
            NsdManager.PROTOCOL_DNS_SD,
            discoveryListener
        )
    }

    private fun resolveService(service: NsdServiceInfo) {
        nsdManager.resolveService(service, resolveListener)
    }

    private fun String.showAsLog() {
        Log.d("ZEROCONF", this)
    }

    companion object {
        private const val SEARCH_SERVICE_TYPE = "_ronnnnn._tcp"
    }
}
