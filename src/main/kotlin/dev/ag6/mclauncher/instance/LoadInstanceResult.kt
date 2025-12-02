package dev.ag6.mclauncher.instance

class Error(val message: String? = null, val exception: Exception? = null)

class LoadInstanceResult(val instance: GameInstance?, val error: Error?) {
    fun isSuccess(): Boolean {
        return instance != null && error == null
    }

    companion object {
        fun success(instance: GameInstance): LoadInstanceResult {
            return LoadInstanceResult(instance, null)
        }

        fun failure(error: Error): LoadInstanceResult {
            return LoadInstanceResult(null, error)
        }
    }
}