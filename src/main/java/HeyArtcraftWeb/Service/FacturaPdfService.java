package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.DetallePedido;
import HeyArtcraftWeb.Domain.Pedido;
import HeyArtcraftWeb.Domain.Usuario;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Módulo 7 - HU-19: arma la factura del pedido como un PDF real en el
 * servidor, para que el navegador la descargue como archivo.
 *
 * Los montos se rotulan con el código CRC en vez del símbolo del colón
 * porque las fuentes base del PDF (Helvetica) no incluyen ese glifo.
 */
@Service
public class FacturaPdfService {

    private static final Color CAFE = new Color(58, 36, 24);
    private static final Color VERDE = new Color(79, 79, 53);
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private final MessageSource messageSource;

    public FacturaPdfService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public byte[] generarFactura(Pedido pedido) {
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Document documento = new Document(PageSize.LETTER, 45, 45, 45, 45);
            PdfWriter.getInstance(documento, salida);
            documento.open();

            documento.add(parrafo("Hey Artcraft", 20, true, CAFE, Element.ALIGN_CENTER, 0f, 2f));
            documento.add(parrafo(texto("factura.titulo"), 13, true, VERDE, Element.ALIGN_CENTER, 0f, 16f));
            documento.add(tablaDatosPedido(pedido));
            documento.add(parrafo(texto("factura.productos"), 11, true, CAFE, Element.ALIGN_LEFT, 14f, 6f));
            documento.add(tablaProductos(pedido));
            documento.add(tablaTotales(pedido));

            documento.close();
            return salida.toByteArray();
        } catch (IOException | DocumentException e) {
            throw new IllegalStateException(
                    "No se pudo generar la factura del pedido " + pedido.getId(), e);
        }
    }

    /** Número de pedido, estado, fechas y datos del cliente. */
    private PdfPTable tablaDatosPedido(Pedido pedido) {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);

        Usuario cliente = pedido.getUsuario();
        String nombreCliente = cliente == null
                ? "-" : cliente.getNombre() + " " + cliente.getApellidos();
        String correoCliente = cliente == null ? "-" : cliente.getCorreo();

        tabla.addCell(celdaDato(texto("factura.numero"), "#" + pedido.getId()));
        tabla.addCell(celdaDato(texto("pedido.estado"), pedido.getEstado()));
        tabla.addCell(celdaDato(texto("pedido.fechaEmision"), fecha(pedido.getFechaCreacion())));
        tabla.addCell(celdaDato(texto("pedido.fechaEntrega"),
                pedido.getFechaEntrega() == null
                        ? texto("pedido.sinEntrega")
                        : fecha(pedido.getFechaEntrega())));
        tabla.addCell(celdaDato(texto("factura.cliente"), nombreCliente));
        tabla.addCell(celdaDato(texto("usuario.correo"), correoCliente));

        return tabla;
    }

    private PdfPTable tablaProductos(Pedido pedido) {
        PdfPTable tabla = new PdfPTable(new float[]{40f, 32f, 10f, 18f});
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(12f);

        tabla.addCell(celdaCabecera(texto("producto.nombre"), Element.ALIGN_LEFT));
        tabla.addCell(celdaCabecera(texto("factura.detalle"), Element.ALIGN_LEFT));
        tabla.addCell(celdaCabecera(texto("factura.cantidad"), Element.ALIGN_CENTER));
        tabla.addCell(celdaCabecera(texto("factura.precioUnitario"), Element.ALIGN_RIGHT));

        for (DetallePedido detalle : pedido.getDetalles()) {
            tabla.addCell(celdaCuerpo(detalle.getNombreProducto(), Element.ALIGN_LEFT));
            tabla.addCell(celdaCuerpo(personalizacion(detalle), Element.ALIGN_LEFT));
            // Cada línea del carrito corresponde a una pieza personalizada.
            tabla.addCell(celdaCuerpo("1", Element.ALIGN_CENTER));
            tabla.addCell(celdaCuerpo(monto(detalle.getPrecioUnitario()), Element.ALIGN_RIGHT));
        }
        return tabla;
    }

    private PdfPTable tablaTotales(Pedido pedido) {
        PdfPTable tabla = new PdfPTable(new float[]{70f, 30f});
        tabla.setWidthPercentage(100);

        tabla.addCell(celdaTotal(texto("factura.subtotal"), false));
        tabla.addCell(celdaTotal(monto(pedido.getSubtotal()), false));

        if (pedido.isIncluyeEnvio()) {
            tabla.addCell(celdaTotal(texto("factura.envio"), false));
            tabla.addCell(celdaTotal(monto(pedido.getCostoEnvio()), false));
        }

        tabla.addCell(celdaTotal(texto("factura.total"), true));
        tabla.addCell(celdaTotal(monto(pedido.getTotal()), true));

        return tabla;
    }

    /** Junta texto personalizado, tamaño y especificaciones en una sola celda. */
    private String personalizacion(DetallePedido detalle) {
        List<String> partes = new ArrayList<>();
        if (tieneValor(detalle.getTextoPersonalizado())) {
            partes.add(detalle.getTextoPersonalizado());
        }
        if (tieneValor(detalle.getTamanoSeleccionado())) {
            partes.add(detalle.getTamanoSeleccionado());
        }
        if (tieneValor(detalle.getEspecificaciones())) {
            partes.add(detalle.getEspecificaciones());
        }
        return partes.isEmpty() ? "-" : String.join(" / ", partes);
    }

    private boolean tieneValor(String valor) {
        return valor != null && !valor.isBlank();
    }

    private PdfPCell celdaDato(String etiqueta, String valor) {
        Phrase frase = new Phrase();
        frase.add(new Chunk(etiqueta + ": ", fuente(9, true, CAFE)));
        frase.add(new Chunk(valor == null ? "-" : valor, fuente(9, false, Color.DARK_GRAY)));

        PdfPCell celda = new PdfPCell(frase);
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setPadding(3f);
        return celda;
    }

    private PdfPCell celdaCabecera(String valor, int alineacion) {
        PdfPCell celda = new PdfPCell(new Phrase(valor, fuente(9, true, Color.WHITE)));
        celda.setBackgroundColor(VERDE);
        celda.setHorizontalAlignment(alineacion);
        celda.setPadding(5f);
        return celda;
    }

    private PdfPCell celdaCuerpo(String valor, int alineacion) {
        PdfPCell celda = new PdfPCell(new Phrase(valor, fuente(9, false, Color.DARK_GRAY)));
        celda.setHorizontalAlignment(alineacion);
        celda.setPadding(5f);
        return celda;
    }

    private PdfPCell celdaTotal(String valor, boolean destacado) {
        PdfPCell celda = new PdfPCell(new Phrase(valor,
                fuente(destacado ? 11 : 9, destacado, destacado ? CAFE : Color.DARK_GRAY)));
        celda.setBorder(destacado ? Rectangle.TOP : Rectangle.NO_BORDER);
        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celda.setPadding(4f);
        return celda;
    }

    private Paragraph parrafo(String valor, int tamano, boolean negrita, Color color,
            int alineacion, float espacioAntes, float espacioDespues) {
        Paragraph parrafo = new Paragraph(valor, fuente(tamano, negrita, color));
        parrafo.setAlignment(alineacion);
        parrafo.setSpacingBefore(espacioAntes);
        parrafo.setSpacingAfter(espacioDespues);
        return parrafo;
    }

    private Font fuente(int tamano, boolean negrita, Color color) {
        return FontFactory.getFont(
                negrita ? FontFactory.HELVETICA_BOLD : FontFactory.HELVETICA, tamano, color);
    }

    private String monto(BigDecimal valor) {
        BigDecimal seguro = valor == null ? BigDecimal.ZERO : valor;
        return "CRC " + String.format(Locale.US, "%,.2f", seguro);
    }

    private String fecha(LocalDateTime valor) {
        return valor == null ? "-" : valor.format(FORMATO_FECHA);
    }

    private String texto(String clave) {
        return messageSource.getMessage(clave, null, clave, Locale.getDefault());
    }
}
