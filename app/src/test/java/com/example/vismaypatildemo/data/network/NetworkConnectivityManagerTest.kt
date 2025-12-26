package com.example.vismaypatildemo.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.example.vismaypatildemo.core.NetworkConnectivityManager
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class NetworkConnectivityManagerTest {

    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkConnectivityManager: NetworkConnectivityManager

    @Before
    fun setup() {
        context = mockk()
        connectivityManager = mockk()
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager

        networkConnectivityManager = NetworkConnectivityManager(context)
    }

    @Test
    fun `isNetworkAvailable returns true when network is available and validated`() {
        val network: Network = mockk()
        val capabilities: NetworkCapabilities = mockk()

        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns true

        val result = networkConnectivityManager.isNetworkAvailable()

        assertThat(result).isTrue()
    }

    @Test
    fun `isNetworkAvailable returns false when no active network`() {
        every { connectivityManager.activeNetwork } returns null

        val result = networkConnectivityManager.isNetworkAvailable()

        assertThat(result).isFalse()
    }

    @Test
    fun `isNetworkAvailable returns false when network capabilities are null`() {
        val network: Network = mockk()
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns null

        val result = networkConnectivityManager.isNetworkAvailable()

        assertThat(result).isFalse()
    }

    @Test
    fun `isNetworkAvailable returns false when internet capability is missing`() {
        val network: Network = mockk()
        val capabilities: NetworkCapabilities = mockk()

        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns false
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns true

        val result = networkConnectivityManager.isNetworkAvailable()

        assertThat(result).isFalse()
    }

    @Test
    fun `isNetworkAvailable returns false when network is not validated`() {
        val network: Network = mockk()
        val capabilities: NetworkCapabilities = mockk()

        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns false

        val result = networkConnectivityManager.isNetworkAvailable()

        assertThat(result).isFalse()
    }
}