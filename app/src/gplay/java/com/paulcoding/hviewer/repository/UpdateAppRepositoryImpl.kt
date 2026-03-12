import com.paulcoding.hviewer.model.HRelease
import com.paulcoding.hviewer.repository.UpdateAppRepository
import java.io.File


class UpdateAppRepositoryImpl(
) : UpdateAppRepository {
    override suspend fun getLatestAppRelease() = Result.failure<HRelease>(Exception("Not implemented"))

    override suspend fun downloadApk(downloadUrl: String, destination: File) =
        Result.failure<File>(Exception("Not implemented"))
}