class Parente(val parentesco: String) : Imprimível {
    // Implementação do método imprimir da interface Imprimível
    override fun imprimir() {
        println("Parente com o parentesco: $parentesco")
    }
}