package com.terabyte.jetpackqrscanner

import android.app.Application
import androidx.room.Room
import com.terabyte.jetpackqrscanner.data.MainDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideMainDB(application: Application): MainDB {
        return Room.databaseBuilder(
            application,
            MainDB::class.java,
            "products.db"
        ).build()
    }
}