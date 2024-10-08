package dev.nyon.bbm.config

/*? if fabric && >=1.20.1 {*/
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi

@Suppress("unused")
class ModMenuImpl : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory {
            generateYaclScreen(it)
        }
    }
}
/*?}*/