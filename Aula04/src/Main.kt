import java.util.Date
import java.text.SimpleDateFormat


fun main() {
    val endereco = Endereco("Manuel Ribas", 123, "12345-678", "Curitiba", "PR")
    println("Detalhes do Endereço:")
    endereco.imprime()

    // Criação de instâncias de Amigo com datas específicas
    val sdf = SimpleDateFormat("dd-MM-yyyy")
    val amigo1 = Amigo(sdf.parse("31-05-2022"))
    val amigo2 = Amigo(sdf.parse("27-02-2022"))

    // Impressão das datas
    println("\nAmigos cadastrados:")
    amigo1.imprimir()
    amigo2.imprimir()

    val parente1 = Parente("Tio")
    val parente2 = Parente("Prima")

    // Imprimindo informações dos parentes
    println("\nParentes cadastrados:")
    parente1.imprimir()
    parente2.imprimir()

    // Criando uma instância da classe Contato
    val contato = Contato("João", 30, 175f, 'M', "joao@gmail.com")
    println("\nDetalhes do Contato:")
    contato.imprimir()
}
