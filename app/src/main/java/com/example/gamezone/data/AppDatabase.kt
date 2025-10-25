package com.example.gamezone.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context

@Database(
    entities = [
        User::class,
        Game::class,
        Cart::class,
        CartItem::class,
        Order::class,
        OrderItem::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun gameDao(): GameDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gamezone_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create new tables for games, cart, and orders
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS games (
                        id TEXT PRIMARY KEY NOT NULL,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        longDescription TEXT NOT NULL,
                        price REAL NOT NULL,
                        category TEXT NOT NULL,
                        rating REAL NOT NULL,
                        releaseDate TEXT NOT NULL,
                        developer TEXT NOT NULL,
                        publisher TEXT NOT NULL,
                        imageResId INTEGER NOT NULL,
                        isAvailable INTEGER NOT NULL DEFAULT 1,
                        createdAt INTEGER NOT NULL
                    )
                """)
                
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS carts (
                        id TEXT PRIMARY KEY NOT NULL,
                        userId TEXT NOT NULL,
                        totalAmount REAL NOT NULL DEFAULT 0.0,
                        itemCount INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
                
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS cart_items (
                        id TEXT PRIMARY KEY NOT NULL,
                        cartId TEXT NOT NULL,
                        gameId TEXT NOT NULL,
                        quantity INTEGER NOT NULL DEFAULT 1,
                        price REAL NOT NULL,
                        addedAt INTEGER NOT NULL,
                        FOREIGN KEY(cartId) REFERENCES carts(id) ON DELETE CASCADE,
                        FOREIGN KEY(gameId) REFERENCES games(id) ON DELETE CASCADE
                    )
                """)
                
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS orders (
                        id TEXT PRIMARY KEY NOT NULL,
                        userId TEXT NOT NULL,
                        totalAmount REAL NOT NULL,
                        status TEXT NOT NULL DEFAULT 'pending',
                        shippingAddress TEXT NOT NULL,
                        paymentMethod TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """)
                
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS order_items (
                        id TEXT PRIMARY KEY NOT NULL,
                        orderId TEXT NOT NULL,
                        gameId TEXT NOT NULL,
                        quantity INTEGER NOT NULL,
                        price REAL NOT NULL,
                        gameName TEXT NOT NULL,
                        FOREIGN KEY(orderId) REFERENCES orders(id) ON DELETE CASCADE,
                        FOREIGN KEY(gameId) REFERENCES games(id) ON DELETE CASCADE
                    )
                """)
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns to users table
                database.execSQL("ALTER TABLE users ADD COLUMN phone TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE users ADD COLUMN profilePictureUri TEXT")
                database.execSQL("ALTER TABLE users ADD COLUMN isAdmin INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE users ADD COLUMN bio TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE users ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
            }
        }



    }
}
