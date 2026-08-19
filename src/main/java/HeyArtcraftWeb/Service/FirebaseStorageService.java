/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package HeyArtcraftWeb.Service;

/**
 *
 * @author natts
 */

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FirebaseStorageService {

    private static final Set<String> TIPOS_PERMITIDOS
            = Set.of("image/jpeg", "image/png");

    private static final long TAMANO_MAXIMO
            = 10L * 1024L * 1024L;

    private final FirebaseApp firebaseApp;

    public FirebaseStorageService(FirebaseApp firebaseApp) {
        this.firebaseApp = firebaseApp;
    }

    public String subirImagen(MultipartFile imagen) {
        validarImagen(imagen);

        Bucket bucket = StorageClient.getInstance(firebaseApp).bucket();
        String extension = obtenerExtension(imagen.getContentType());
        String nombreObjeto = "productos/"
                + UUID.randomUUID()
                + extension;

        String tokenDescarga = UUID.randomUUID().toString();

        BlobInfo blobInfo = BlobInfo.newBuilder(
                bucket.getName(), nombreObjeto)
                .setContentType(imagen.getContentType())
                .setMetadata(Map.of(
                        "firebaseStorageDownloadTokens",
                        tokenDescarga))
                .build();

        try {
            bucket.getStorage().create(blobInfo, imagen.getBytes());
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo subir la imagen a Firebase Storage", e);
        }

        String nombreCodificado = URLEncoder.encode(
                nombreObjeto, StandardCharsets.UTF_8)
                .replace("+", "%20");

        return "https://firebasestorage.googleapis.com/v0/b/"
                + bucket.getName()
                + "/o/"
                + nombreCodificado
                + "?alt=media&token="
                + tokenDescarga;
    }

    public void eliminarImagen(String imagenUrl) {
        if (imagenUrl == null
                || imagenUrl.isBlank()
                || !imagenUrl.contains(
                        "firebasestorage.googleapis.com")) {
            return;
        }

        String marcador = "/o/";
        int inicio = imagenUrl.indexOf(marcador);

        if (inicio < 0) {
            return;
        }

        inicio += marcador.length();
        int fin = imagenUrl.indexOf("?", inicio);

        String nombreCodificado = fin >= 0
                ? imagenUrl.substring(inicio, fin)
                : imagenUrl.substring(inicio);

        String nombreObjeto = URLDecoder.decode(
                nombreCodificado, StandardCharsets.UTF_8);

        Bucket bucket = StorageClient.getInstance(firebaseApp).bucket();

        bucket.getStorage().delete(
                BlobId.of(bucket.getName(), nombreObjeto));
    }

    private void validarImagen(MultipartFile imagen) {
        if (imagen == null || imagen.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una imagen");
        }

        if (!TIPOS_PERMITIDOS.contains(imagen.getContentType())) {
            throw new IllegalArgumentException(
                    "Solo se permiten imágenes JPG o PNG");
        }

        if (imagen.getSize() > TAMANO_MAXIMO) {
            throw new IllegalArgumentException(
                    "La imagen no puede superar los 10 MB");
        }
    }

    private String obtenerExtension(String contentType) {
        return "image/png".equals(contentType)
                ? ".png"
                : ".jpg";
    }
}
