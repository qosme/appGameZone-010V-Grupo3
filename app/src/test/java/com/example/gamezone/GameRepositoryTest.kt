package com.example.gamezone

import com.example.gamezone.data.Game
import com.example.gamezone.data.GameDao
import com.example.gamezone.data.GameRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals  // Use JUnit 5 assertions
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk


class GameRepositoryTest {

    private lateinit var gameRepository: GameRepository
    private lateinit var gameDao: GameDao

    @BeforeEach
    fun setup() {
        // Crear mock de gameDao
        gameDao = mockk()

        // Instanciar gameRepo con gameDao mock
        gameRepository = GameRepository(gameDao)
    }

    @Test
    fun `insertGame debe llamar insertGame en la Dao`() = runTest {
        // Dado
        val mockGame = Game(
            id = "1",
            name = "Juego 1",
            description = "Descripcion 1",
            longDescription = "Descripcion larga para Juego 1",
            price = 100.0,
            category = "Accion",
            rating = 4.5,
            releaseDate = "2021-01-01",
            developer = "Dev 1",
            publisher = "Pub 1",
            imageResId = 1
        )

        // Mockear el comportamiento de insertGame para que no de errores
        coEvery { gameDao.insertGame(mockGame) } returns Unit  // Unit se usa para metodos sin return

        // Cuando se insertan
        gameRepository.insertGame(mockGame)

        // Entonces verificar que insertGame en llamo en la dao con el juego correcto
        coVerify { gameDao.insertGame(mockGame) }
    }

    @Test
    fun `updateGame debe llamar updateGame en la DAO`() = runTest {
        // Dado
        val mockGame = Game(
            id = "1",
            name = "Juego 1",
            description = "Descripcion 1",
            longDescription = "Descripcion larga para Juego 1",
            price = 100.0,
            category = "Accion",
            rating = 4.5,
            releaseDate = "2021-01-01",
            developer = "Dev 1",
            publisher = "Pub 1",
            imageResId = 1
        )

        // Mockear el comportamiento de updateGame para que no de errores
        coEvery { gameDao.updateGame(mockGame) } returns Unit

        // Cuando se llama a update
        gameRepository.updateGame(mockGame)

        // Entonces verificar que el juego del update es el correcto
        coVerify { gameDao.updateGame(mockGame) }
    }

    @Test
    fun `deleteGame debe llamar a deleteGame en la DAO`() = runTest {
        // Dado
        val mockGame = Game(
            id = "1",
            name = "Juego 1",
            description = "Descripcion 1",
            longDescription = "Descripcion larga para Juego 1",
            price = 100.0,
            category = "Accion",
            rating = 4.5,
            releaseDate = "2021-01-01",
            developer = "Dev 1",
            publisher = "Pub 1",
            imageResId = 1
        )

        // Mockear el comportamiento de deleteGame para que no de errores
        coEvery { gameDao.deleteGame(mockGame) } returns Unit

        // Cuando se llama al metodo
        gameRepository.deleteGame(mockGame)

        // Entonces se verifica que se borro el juego correcto
        coVerify { gameDao.deleteGame(mockGame) }
    }

    @Test
    fun `deleteGameById debe llamar a deleteGameById en la DAO`() = runTest {
        // Dado
        val gameId = "1"

        // Mockear el comportamiento de deleteGameById para que no salgan errores
        coEvery { gameDao.deleteGameById(gameId) } returns Unit

        // Cuando se llama el metodo
        gameRepository.deleteGameById(gameId)

        // Entonces verficar que el juego llamado por el metodo tiene el id correcto
        coVerify { gameDao.deleteGameById(gameId) }
    }
}
