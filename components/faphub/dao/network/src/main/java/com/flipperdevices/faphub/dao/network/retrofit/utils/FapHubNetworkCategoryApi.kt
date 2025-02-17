package com.flipperdevices.faphub.dao.network.retrofit.utils

import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.network.retrofit.api.KtorfitCategoryApi
import com.flipperdevices.faphub.dao.network.retrofit.model.KtorfitCategory
import com.flipperdevices.faphub.target.model.FlipperTarget
import kotlinx.coroutines.sync.Mutex

class FapHubNetworkCategoryApi(
    private val categoryApi: KtorfitCategoryApi
) : LogTagProvider {
    override val TAG = "FapHubNetworkCategoryApi"

    private val mutex = Mutex()
    private var currentTarget: FlipperTarget? = null
    private var categories: Map<String, KtorfitCategory>? = null

    suspend fun get(target: FlipperTarget, id: String): FapCategory? {
        invalidateIfNeed(target)
        return categories?.get(id)?.toFapCategory()
    }

    suspend fun getAll(target: FlipperTarget): List<KtorfitCategory> {
        invalidateIfNeed(target)
        return categories?.map { it.value } ?: error("Failed receive categories")
    }

    private suspend fun invalidateIfNeed(target: FlipperTarget) = withLock(mutex, "invalidate") {
        if (!shouldInvalidate(target)) {
            return@withLock
        }
        val categoriesReceived = try {
            categoryApi.getAll(
                sdkApi = target.getApiForServer()
            )
        } catch (ex: Exception) {
            error(ex) { "Failed get categories" }
            return@withLock
        }
        info { "Received ${categoriesReceived.size} categories" }
        categories = categoriesReceived.associateBy { it.id }
        currentTarget = target
    }

    private fun shouldInvalidate(target: FlipperTarget): Boolean {
        if (currentTarget != target) {
            return true
        }
        if (categories == null) {
            return true
        }
        info { "Skip invalidate because target is same and categories exist" }
        return false
    }
}
