package com.johannjara.docvault.core.location

import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocationProviderTest {

    private val context: Context = mockk()
    private val fusedLocationClient: FusedLocationProviderClient = mockk()
    private val geocoder: Geocoder = mockk()
    private lateinit var locationProvider: LocationProviderImpl

    @Before
    fun setup() {
        mockkStatic(LocationServices::class)
        every { LocationServices.getFusedLocationProviderClient(context) } returns fusedLocationClient

        mockkStatic("kotlinx.coroutines.tasks.TasksKt")

        locationProvider = LocationProviderImpl(context)
    }

    @After
    fun tearDown() {
        unmockkStatic(LocationServices::class)
        unmockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    @Test
    fun `getCurrentLocationName should return street name when location and address are available`() =
        runTest {
            val location: Location = mockk()
            every { location.latitude } returns 10.0
            every { location.longitude } returns 20.0

            val task: Task<Location> = mockk()
            every {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                )
            } returns task
        }
}
