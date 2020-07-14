package com.sherepenko.android.measureit.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sherepenko.android.measureit.data.HumidityItem
import com.sherepenko.android.measureit.data.PressureItem
import com.sherepenko.android.measureit.data.Resource
import com.sherepenko.android.measureit.data.TemperatureItem
import com.sherepenko.android.measureit.repositories.HumidityRepository
import com.sherepenko.android.measureit.repositories.PressureRepository
import com.sherepenko.android.measureit.repositories.TemperatureRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class DashboardViewModel(
    private val humidityRepository: HumidityRepository,
    private val pressureRepository: PressureRepository,
    private val temperatureRepository: TemperatureRepository
) : ViewModel() {

    private val disposable = CompositeDisposable()

    private val humidity = MutableLiveData<Resource<HumidityItem>>()

    private val pressure = MutableLiveData<Resource<PressureItem>>()

    private val temperature = MutableLiveData<Resource<TemperatureItem>>()

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun getHumidity(): LiveData<Resource<HumidityItem>> {
        humidityRepository
            .getHumidity()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                humidity.postValue(Resource.loading())
            }
            .subscribeBy(
                onNext = {
                    humidity.postValue(Resource.success(it))
                },
                onError = {
                    humidity.postValue(Resource.error(it))
                }
            )
            .addTo(disposable)

        return humidity
    }

    fun getPressure(): LiveData<Resource<PressureItem>> {
        pressureRepository
            .getPressure()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                pressure.postValue(Resource.loading())
            }
            .subscribeBy(
                onNext = {
                    pressure.postValue(Resource.success(it))
                },
                onError = {
                    pressure.postValue(Resource.error(it))
                }
            )
            .addTo(disposable)

        return pressure
    }

    fun getTemperature(): LiveData<Resource<TemperatureItem>> {
        temperatureRepository
            .getTemperature()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                temperature.postValue(Resource.loading())
            }
            .subscribeBy(
                onNext = {
                    temperature.postValue(Resource.success(it))
                },
                onError = {
                    temperature.postValue(Resource.error(it))
                }
            )
            .addTo(disposable)

        return temperature
    }
}
