package com.ronnnnn.zeroconfsample

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ronnnnn.zeroconfsample.databinding.ActivityHostBinding
import java.net.ServerSocket

class HostActivity : AppCompatActivity() {

    private var isServiceRegistered: Boolean = false

    private val nsdManager: NsdManager by lazy {
        getSystemService(Context.NSD_SERVICE) as? NsdManager
            ?: throw IllegalStateException("can't get NsdManager")
    }

    private val registrationListener: NsdManager.RegistrationListener =
        object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(serviceInfo: NsdServiceInfo?) {
                "Service register".showAsLog()
                isServiceRegistered = true
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                "Service register failed".showAsLog()
            }

            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo?) {
                "Service unregister".showAsLog()
                isServiceRegistered = false
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                "Service unregister fail".showAsLog()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityHostBinding>(
            this,
            R.layout.activity_host
        ).apply {
            startServiceButton.setOnClickListener {
                registerService()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceRegistered) nsdManager.unregisterService(registrationListener)
    }

    private fun registerService() {
        val serviceInfo = NsdServiceInfo().apply {
            port = ServerSocket(0).localPort
            serviceType = HOST_SERVICE_TYPE
            serviceName = HOST_SERVICE_NAME
        }
        nsdManager.registerService(
            serviceInfo,
            NsdManager.PROTOCOL_DNS_SD,
            registrationListener
        )
    }

    private fun String.showAsLog() {
        Log.d("ZEROCONF", this)
    }

    companion object {
        private const val HOST_SERVICE_TYPE = "_ronnnnn._tcp"
        private val HOST_SERVICE_NAME = Build.MODEL
    }
}