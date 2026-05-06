package net.donutsmp.companion.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class AuctionMonitorWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = Result.success()
}
