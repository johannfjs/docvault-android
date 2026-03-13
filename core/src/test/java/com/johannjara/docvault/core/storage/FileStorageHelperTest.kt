package com.johannjara.docvault.core.storage

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.johannjara.docvault.core.security.FileEncryptor
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldExist
import org.amshove.kluent.shouldNotExist
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class FileStorageHelperTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val context: Context = mockk()
    private val contentResolver: ContentResolver = mockk()
    private val fileEncryptor: FileEncryptor = mockk()
    private val dispatcher = Dispatchers.Unconfined

    private lateinit var fileStorageHelper: FileStorageHelper
    private lateinit var filesDir: File

    @Before
    fun setup() {
        filesDir = temporaryFolder.newFolder("files")
        every { context.filesDir } returns filesDir
        every { context.contentResolver } returns contentResolver
        
        fileStorageHelper = FileStorageHelperImpl(context, fileEncryptor, dispatcher)
    }

    @Test
    fun `saveAndEncryptFile should copy and encrypt file to secure folder`() = runTest {
        val fileName = "test_file.txt"
        val content = "Hello Secure World"
        val uri: Uri = mockk()
        val inputStream = ByteArrayInputStream(content.toByteArray())
        
        every { contentResolver.openInputStream(uri) } returns inputStream
        
        val secureVaultDir = File(filesDir, "secure_vault")
        secureVaultDir.mkdirs()
        val targetFile = File(secureVaultDir, fileName)

        every { fileEncryptor.getEncryptedOutputStream(any()) } answers {
            FileOutputStream(it.invocation.args[0] as File)
        }

        val result = fileStorageHelper.saveAndEncryptFile(uri, fileName)

        result shouldBeEqualTo targetFile
        result?.shouldExist()
    }

    @Test
    fun `getFile should return file in secure folder`() {
        val fileName = "my_doc.pdf"
        val expectedFile = File(File(filesDir, "secure_vault"), fileName)

        val result = fileStorageHelper.getFile(fileName)

        result.absolutePath shouldBeEqualTo expectedFile.absolutePath
    }

    @Test
    fun `deleteFile should remove file if it exists`() {
        val fileName = "delete_me.txt"
        val secureVaultDir = File(filesDir, "secure_vault")
        secureVaultDir.mkdirs()
        val fileToDelete = File(secureVaultDir, fileName)
        fileToDelete.createNewFile()
        fileToDelete.shouldExist()

        val result = fileStorageHelper.deleteFile(fileName)

        result shouldBe true
        fileToDelete.shouldNotExist()
    }

    @Test
    fun `deleteFile should return false if file does not exist`() {
        val fileName = "non_existent.txt"

        val result = fileStorageHelper.deleteFile(fileName)

        result shouldBe false
    }
}
