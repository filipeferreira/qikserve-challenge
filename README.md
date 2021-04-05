# qikserve challenge

- To run the application execute in a terminal the following command: ./mvnw spring-boot:run

# API methods

### Products

- get all products: GET http://localhost:8080/products
- get product by id: GET http://localhost:8080/products/{idProduct}

Avaliable product's ids: Dwt5F7KAhi, PWWe3w1SDU, C8GDyLrHJb and 4MB7UfpTQs

### Promotions

- get all promotions: GET http://localhost:8080/promotions
- create new promotion: POST http://localhost:8080/promotions

header Content-type: application/json 

body {
         "code" : "SAVE_100",
         "discount" : 100,
         "expiration" : "2021-06-01"
     }
     
### Baskets

- create new basket: POST http://localhost:8080/baskets
- get basket by id: GET http://localhost:8080/baskets/{idBasket}
- add item: POST http://localhost:8080/baskets/{idBasket}/add-item

header Content-type: application/json

body {
         "idProduct" : "4MB7UfpTQs",
         "amount" : 1
     }
     
- add promotion to a basket http://localhost:8080/baskets/{idBasket}/add-promotion/{code}
- checkout a basket http://localhost:8080/baskets/{idBasket}/checkout
