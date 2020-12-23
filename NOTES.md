### @Query JPQL
Using distinct to fetch relations - https://in.relation.to/2016/08/04/introducing-distinct-pass-through-query-hint/
No limit keyword - use Pageable instead

### Criteria API
We cannot get projection of relation that match predicate https://stackoverflow.com/questions/36452958/jpa-criteria-query-for-one-to-many-relation-with-predicate-on-child-not-working  
Possible workaround - query child relation with parent fetching
 
 
Read about 1-to-N relations - https://medium.com/@rajibrath20/the-best-way-to-map-a-onetomany-relationship-with-jpa-and-hibernate-dbbf6dba00d3

### Native queries (via Hibernate)
At this stage, you are reaching the limits of what is possible with native queries, without starting to enhance the sql queries to make them usable in Hibernate. Problems can arise when returning multiple entities of the same type or when the default alias/column names are not enough.https://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/Hibernate_User_Guide.html#sql-entity-associations-query  
You can't join fetch using baic JPA https://stackoverflow.com/questions/7195549/jpa-native-join-fetch  
Native query doesn't work ok with @embedded entities