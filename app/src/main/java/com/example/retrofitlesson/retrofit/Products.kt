package com.example.retrofitlesson.retrofit

data class Products(
    val products: List<Product>,
    val total: Int,
    val skip: Int,
    val limit: Int,
)
