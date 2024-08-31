import java.text.SimpleDateFormat
import java.util.Date

class Amigo(val conhecidoEm: Date) : Imprimível {
    override fun imprimir() {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        println("Amigo conhecido em: ${sdf.format(conhecidoEm)}")
    }
}