package com.wkerein.espada.core

import com.wkerein.espada.Config.Database.driver
import com.wkerein.espada.Config.Database.password
import com.wkerein.espada.Config.Database.url
import com.wkerein.espada.Config.Database.user


object Database {
    val defaultDatabase = org.ktorm.database.Database.connect(
        url = url,
        driver = driver,
        user = user,
        password = password
    )
}