class Endereco(val run: String, val numero: Int, val cep: String, val cidade: String, val estado: String) {
    // Método para imprimir os detalhes do endereço
    fun imprime() {
        println("Endereço: $run, Nº $numero, CEP: $cep, Cidade: $cidade, Estado: $estado")
    }
}