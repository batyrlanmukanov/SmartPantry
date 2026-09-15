package kz.kbtu.smartpantry.core.data

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kz.kbtu.smartpantry.core.database.FoodDao
import kz.kbtu.smartpantry.core.database.SmartPantryDatabase
import kz.kbtu.smartpantry.core.domain.AuthRepository
import kz.kbtu.smartpantry.core.domain.FoodRepository
import kz.kbtu.smartpantry.core.domain.ProfileRepository
import kz.kbtu.smartpantry.core.domain.ShoppingListRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindings {
    @Binds
    abstract fun bindAuthRepository(impl: LocalAuthRepository): AuthRepository

    @Binds
    abstract fun bindProfileRepository(impl: LocalProfileRepository): ProfileRepository

    @Binds
    abstract fun bindFoodRepository(impl: OfflineFoodRepository): FoodRepository

    @Binds
    abstract fun bindShoppingListRepository(impl: LocalShoppingListRepository): ShoppingListRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SmartPantryDatabase =
        Room.databaseBuilder(
            context,
            SmartPantryDatabase::class.java,
            "smart_pantry.db",
        ).build()

    @Provides
    fun provideFoodDao(database: SmartPantryDatabase): FoodDao = database.foodDao()
}
