package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.KdfArchitectureCatalog
import com.example.data.KdfScriptEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `verify app name and KDF-LuX scripts and yaml initialization`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("KDF-Tools", appName)

        // Verify 6 device categories and maintenance/utility operations
        assertEquals(6, KdfArchitectureCatalog.deviceModules.size)
        assertEquals(9, KdfArchitectureCatalog.maintenanceOperations.size)

        // Verify KdfScriptEngine creates executable scripts and categories.yaml
        val engine = KdfScriptEngine(context)
        val requiredScripts = listOf(
            "enter_safe_mode.sh",
            "disable_blocker.sh",
            "clean_memory.sh",
            "unlock_bootloader.sh",
            "edl_flasher.sh",
            "flash_aosp_gsi.sh",
            "unbrick_recovery.sh",
            "low_level_diagnostic.sh"
        )
        requiredScripts.forEach { scriptName ->
            val file = File(engine.scriptsDir, scriptName)
            assertTrue("Script $scriptName should exist", file.exists())
            assertTrue("Script $scriptName should be executable", file.canExecute())
        }

        val categoriesYaml = File(engine.configDir, "categories.yaml")
        assertTrue("categories.yaml should exist", categoriesYaml.exists())
        val yamlContent = categoriesYaml.readText()
        assertTrue(yamlContent.contains("mode_safe"))
        assertTrue(yamlContent.contains("app_blocker"))
        assertTrue(yamlContent.contains("mem_cleaner"))
        assertTrue(yamlContent.contains("bootloader_unlock"))
        assertTrue(yamlContent.contains("edl_flasher"))
    }
}
