## Follow up questions

- ####How long did you spend on the test?

I spent a week on the test. 

- ####What would you add if you had more time?

If I had more time, I would have done:

1) Persistence layer for all model objects using JPA/Hibernate, Spring Data JPA
2) Security layer using spring-security-oauth2
3) Auditing transactions using spring-data-envers
4) Web frontend
5) Sign up feature for new users
6) Sign in feature for current users
7) Administration layer: add products, promotions for admin users
8) Basket analysis and sales metrics: based on technical specification of the supermarket team 
9) Swagger to visualize and interact with the Rest API
10) Delete basket's products
11) Increase or decrease the amount of the basket's products

- ####How would you improve the product APIs that you had to consume?

1) Update products attributes
2) Add attributes like brand, supplier

- ####What did you find most difficult?

I thought that creating features based on unclear user stories was the most difficult part of the challenge.

- ####How did you find the overall experience, any feedback for us?

I've enjoyed doing the challenge, but I've never used wiremock before. 

What happened is that I've just checked all the mappings of wiremock when I was integrating wiremock with my e2e tests, at the end of the project. That's the reason of my mistake on the business logic of products. In my implementation, promotions belong to a basket instead of products.

When I read in the document challenge, this part of the text "Think about how the solution would be used to calculate the total cost of a basket which could contain any combination of items and promotions", I've started modelling a basket having a set of promotions and a set of items. A promotion, I thought that could be an object having a code, discount and an expiration date, and that's the way that I've implemented. I've just realized that a promotion belongs to a product today (2021-04-04).


