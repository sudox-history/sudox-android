package ru.sudox.phone

import android.content.res.AssetManager
import com.google.i18n.phonenumbers.MetadataLoader
import java.io.InputStream

/**
 * Реализует более быструю загрузку ресурсов библиотеки libphonenumber за счет
 * использования нативного AssetManager, а не Class.getResourcesAsStream
 *
 * @param assetManager Менеджер ресурсов приложения.
 */
class AssetsMetadataLoader(
    private val assetManager: AssetManager
) : MetadataLoader {

    override fun loadMetadata(metadataFileName: String): InputStream {
        var path = metadataFileName

        if (path.startsWith("/")) {
            path = path.removeRange(0, 1)
        }

        return assetManager.open(path)
    }
}