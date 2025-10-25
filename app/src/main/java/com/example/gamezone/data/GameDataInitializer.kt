package com.example.gamezone.data

import com.example.gamezone.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameDataInitializer @Inject constructor(
    private val gameRepository: GameRepository
) {
    
    suspend fun initializeGameData() {
        val games = listOf(
            Game(
                id = "1",
                name = "Grand Theft Auto V",
                description = "El juego de mundo abierto más vendido de la historia",
                longDescription = "Grand Theft Auto V es un videojuego de acción-aventura de mundo abierto desarrollado por Rockstar North y publicado por Rockstar Games. Ambientado en la ciudad ficticia de Los Santos, el juego sigue las historias entrelazadas de tres protagonistas: Michael De Santa, Franklin Clinton y Trevor Philips.",
                price = 59990.0,
                category = "Acción",
                rating = 4.5,
                releaseDate = "2013-09-17",
                developer = "Rockstar North",
                publisher = "Rockstar Games",
                imageResId = R.drawable.gtav
            ),
            Game(
                id = "2",
                name = "The Sims 4",
                description = "Crea y controla la vida de tus Sims",
                longDescription = "The Sims 4 es un videojuego de simulación social desarrollado por Maxis y publicado por Electronic Arts. Es la cuarta entrega principal de la serie The Sims. El juego permite a los jugadores crear y controlar personajes virtuales llamados Sims, construir casas para ellos y satisfacer sus necesidades básicas.",
                price = 39990.0,
                category = "Simulación",
                rating = 4.2,
                releaseDate = "2014-09-02",
                developer = "Maxis",
                publisher = "Electronic Arts",
                imageResId = R.drawable.sims4
            ),
            Game(
                id = "3",
                name = "Terraria",
                description = "Un juego de aventura y construcción 2D",
                longDescription = "Terraria es un videojuego de acción-aventura, sandbox y supervivencia desarrollado por Re-Logic. El juego presenta elementos de construcción, exploración, combate y supervivencia. Los jugadores pueden cavar, construir, luchar contra jefes y explorar un mundo generado proceduralmente.",
                price = 9990.0,
                category = "Aventura",
                rating = 4.8,
                releaseDate = "2011-05-16",
                developer = "Re-Logic",
                publisher = "Re-Logic",
                imageResId = R.drawable.terraria
            ),
            Game(
                id = "4",
                name = "Portal 2",
                description = "Un puzzle shooter con mecánicas únicas",
                longDescription = "Portal 2 es un videojuego de puzzle en primera persona desarrollado y publicado por Valve Corporation. Es la secuela de Portal (2007). El juego presenta nuevos elementos de juego, personajes y una historia que se desarrolla después de los eventos del primer juego.",
                price = 19990.0,
                category = "Puzzle",
                rating = 4.9,
                releaseDate = "2011-04-19",
                developer = "Valve Corporation",
                publisher = "Valve Corporation",
                imageResId = R.drawable.portal
            ),
            Game(
                id = "5",
                name = "Minecraft",
                description = "El juego de construcción más popular del mundo",
                longDescription = "Minecraft es un videojuego de construcción de tipo sandbox desarrollado por Mojang Studios. El juego permite a los jugadores construir y destruir bloques en un mundo 3D generado proceduralmente. Los jugadores pueden explorar, recolectar recursos, crear herramientas y construir estructuras.",
                price = 26990.0,
                category = "Sandbox",
                rating = 4.7,
                releaseDate = "2011-11-18",
                developer = "Mojang Studios",
                publisher = "Microsoft",
                imageResId = R.drawable.minecraft
            ),
            Game(
                id = "6",
                name = "Europa Universalis IV",
                description = "Un grand strategy game histórico",
                longDescription = "Europa Universalis IV es un videojuego de estrategia en tiempo real desarrollado por Paradox Development Studio y publicado por Paradox Interactive. El juego permite a los jugadores controlar una nación desde 1444 hasta 1821, guiando su desarrollo a través de la historia.",
                price = 39990.0,
                category = "Estrategia",
                rating = 4.3,
                releaseDate = "2013-08-13",
                developer = "Paradox Development Studio",
                publisher = "Paradox Interactive",
                imageResId = R.drawable.europauniv
            ),
            Game(
                id = "7",
                name = "F1 25",
                description = "La experiencia de Fórmula 1 más realista",
                longDescription = "F1 25 es un videojuego de carreras desarrollado por Codemasters y publicado por EA Sports. El juego presenta la temporada 2025 de Fórmula 1 con todos los equipos, pilotos y circuitos oficiales. Incluye modos de carrera, multijugador y una experiencia de simulación realista.",
                price = 69990.0,
                category = "Carreras",
                rating = 4.4,
                releaseDate = "2025-01-01",
                developer = "Codemasters",
                publisher = "EA Sports",
                imageResId = R.drawable.f125
            ),
            Game(
                id = "8",
                name = "Farming Simulator 25",
                description = "Simula la vida de un granjero moderno",
                longDescription = "Farming Simulator 25 es un videojuego de simulación desarrollado por Giants Software. Los jugadores pueden cultivar, criar ganado, manejar maquinaria agrícola y gestionar su propia granja. El juego incluye vehículos y equipos reales de marcas famosas del sector agrícola.",
                price = 49990.0,
                category = "Simulación",
                rating = 4.1,
                releaseDate = "2025-01-01",
                developer = "Giants Software",
                publisher = "Focus Entertainment",
                imageResId = R.drawable.farming
            )
        )
        
        gameRepository.insertGames(games)
    }
}
