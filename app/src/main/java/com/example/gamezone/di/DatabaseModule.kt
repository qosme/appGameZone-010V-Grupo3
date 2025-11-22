package com.example.gamezone.di

import android.content.Context
import com.example.gamezone.data.AppDatabase
import com.example.gamezone.data.UserDao
import com.example.gamezone.data.UserRepository
import com.example.gamezone.data.GameDao
import com.example.gamezone.data.GameRepository
import com.example.gamezone.data.CartDao
import com.example.gamezone.data.CartRepository
import com.example.gamezone.data.OrderDao
import com.example.gamezone.data.OrderRepository
import com.example.gamezone.data.GameDataInitializer
import com.example.gamezone.data.RestDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideGameDao(database: AppDatabase): GameDao {
        return database.gameDao()
    }

    @Provides
    fun provideCartDao(database: AppDatabase): CartDao {
        return database.cartDao()
    }

    @Provides
    fun provideOrderDao(database: AppDatabase): OrderDao {
        return database.orderDao()
    }

    //@Provides
    //@Singleton
    //fun provideUserRepository(userDao: UserDao): UserRepository {
    //    return UserRepository(userDao)
    //}

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao, api: RestDataSource): UserRepository {
        return UserRepository(userDao, api)
    }


    @Provides
    @Singleton
    fun provideGameRepository(gameDao: GameDao): GameRepository {
        return GameRepository(gameDao)
    }

    @Provides
    @Singleton
    fun provideCartRepository(cartDao: CartDao): CartRepository {
        return CartRepository(cartDao)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(orderDao: OrderDao): OrderRepository {
        return OrderRepository(orderDao)
    }

    @Provides
    @Singleton
    fun provideGameDataInitializer(gameRepository: GameRepository): GameDataInitializer {
        return GameDataInitializer(gameRepository)
    }
}
