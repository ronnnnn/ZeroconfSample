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

    private var isServiceDiscovered: Boolean = false

    private val nsdManager: NsdManager by lazy {
        getSystemService(Context.NSD_SERVICE) as? NsdManager
            ?: throw IllegalStateException("can't get NsdManager")
    }
    private val serviceRecyclerAdapter: ServiceRecyclerAdapter by lazy {
        ServiceRecyclerAdapter()
    }

    private lateinit var binding: ActivityGuestBinding

    private val nsdServiceInfoLiveData: MutableLiveData<NsdServiceInfo> = MutableLiveData()
    private val serviceList: MutableList<NsdServiceInfo> = mutableListOf()

    private val discoveryListener: NsdManager.DiscoveryListener =
        object : NsdManager.DiscoveryListener {
            override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
                serviceInfo ?: return
                nsdServiceInfoLiveData.postValue(serviceInfo)
            }

            override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
                "Stop service discovery failed".showAsLog()
            }

            override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
                "Start service discovery failed".showAsLog()
            }

            override fun onDiscoveryStarted(serviceType: String?) {
                "Start service discovery".showAsLog()
                isServiceDiscovered = true
            }

            override fun onDiscoveryStopped(serviceType: String?) {
                "Stop service discovery".showAsLog()
                isServiceDiscovered = false
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
                "Lost service".showAsLog()
            }
        }

    private val resolveListener: NsdManager.ResolveListener =
        object : NsdManager.ResolveListener {
            override fun onResolveFailed(
                serviceInfo: NsdServiceInfo?,
                errorCode: Int
            ) {
                "Resolve service failed: $errorCode".showAsLog()
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                "Resolve service".showAsLog()
                serviceInfo?.toString()?.showAsLog()
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
                        resolveService(serviceList[position])
                    }
                }
            }
            serviceRecyclerView.layoutManager = LinearLayoutManager(this@GuestActivity)
        }

        nsdServiceInfoLiveData.observe(this, Observer { service ->
            serviceList.add(service)
            serviceRecyclerAdapter.itemList = serviceList.map { it.toString() }.toList()
            serviceRecyclerAdapter.notifyItemRangeInserted(serviceList.size - 1, 1)
        })

        discoverServices()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceDiscovered) nsdManager.stopServiceDiscovery(discoveryListener)
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
        private const val SEARCH_SERVICE_TYPE = "_ronnnnn._tcp."
    }
}
