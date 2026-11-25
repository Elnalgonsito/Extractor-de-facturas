# EXTRACTOR DE FACTURAS Y TICKETS (AI VISION)
**Un caso de éxito en Ingeniería de Software e Inteligencia Artificial Aplicada**

---

## 🚀 EL PROYECTO
Desarrollo de una aplicación móvil nativa para Android enfocada en la automatización contable. La aplicación permite digitalizar, extraer, validar y organizar datos fiscales de tickets y facturas (CFDI) en cuestión de segundos, eliminando la captura manual de datos.

## 🧠 ARQUITECTURA TECNOLÓGICA
- **Desarrollo Nativo:** Construida 100% en Kotlin utilizando **Jetpack Compose** para una interfaz de usuario reactiva, moderna y adaptativa a diferentes pantallas.
- **Visión por Computadora (Groq / Qwen Vision):** Integración de Modelos de Lenguaje Grande (LLMs) multimodales. La app "observa" la fotografía del documento, deduce contextos y extrae información estructurada tolerante a ruido, arrugas o mala iluminación.
- **Escáner Híbrido (Google ML Kit):** Implementación de lectura de Códigos QR nativa en el dispositivo, fusionando datos deterministas (URLs de validación del SAT) con la extracción probabilística de la IA.
- **Almacenamiento Local (Room Database):** Base de datos relacional robusta (v6) que permite estructurar y aislar la información mediante un sistema de carpetas dinámico y seguro.

## 💡 FUNCIONALIDADES CLAVE
1. **Doble Motor (Tickets vs Facturas):** La interfaz y el motor de IA mutan dependiendo del documento. Para tickets, extrae métricas de consumo rápido. Para Facturas, extrae el perfil fiscal completo (UUID, RFCs, Uso CFDI, Método/Forma de Pago).
2. **Matemáticas Autónomas:** Si un ticket omite los impuestos, el motor calcula algebraicamente el IVA (16%) y el IEPS (8%) haciendo ingeniería inversa desde el subtotal o el total.
3. **Auditoría Fiscal a un Toque:** Identificación automática del QR oficial del SAT que permite, mediante un botón de acción, verificar la validez legal y el estatus (Vigente/Cancelada) de la factura directamente en el portal del gobierno.
4. **Reportes Ejecutivos Inmediatos:** Generador de resúmenes limpios con doble espaciado para exportar toda la radiografía del documento directamente a través de clientes de correo electrónico corporativos (Outlook/Gmail) incluyendo la fotografía original como archivo adjunto.
5. **Resiliencia Extrema:** Algoritmos de "Auto-Reintento", compresión de mapas de bits (ImageDecoder) para prevención de fugas de memoria (OutOfMemory), y manejo de conexiones HTTP estables para sobrevivir a caídas de red o cuotas saturadas.

---
*"De una simple idea a un gestor documental impulsado por IA. 41 etapas de evolución técnica ininterrumpida."*
