package com.example.ecommercestore.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.example.ecommercestore.firebase.FirebaseCommon
import com.example.ecommercestore.utils.Constants
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabase() = Firebase.firestore

    @Provides
    fun provideIntroductionSp(
        application: Application) = application.getSharedPreferences(
            Constants.INTRUDUCTION_SP,MODE_PRIVATE
    )

    @Provides
    @Singleton
    fun provideFirebaseCommon(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ) = FirebaseCommon(firestore,auth)

    @Provides
    @Singleton
    fun provideStorage() = FirebaseStorage.getInstance().reference
}