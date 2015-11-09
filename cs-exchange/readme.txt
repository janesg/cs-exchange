Credit Suisse : Pre-Interview Exercise
======================================

Requirements: ./requirements/Credit Suisse 2nd Test.pdf

Solution fully implemented as demonstrated by running ExchangeDemo from command line as follows:
    > mvn clean compile
    > mvn exec:exec
    
Issues:

- Performance:
    Extensive use made of BigDecimal to aid floating point arithmetic.
    As BigDecimal is an immutable class many instances are created and destroyed during processing.
    It may be possible to improve memory usage and performance by using an alternative
    approach to the financial calculations involved.
        
- Thread Safety:
    Exchange class uses multiple collections internally to track open orders and executions.
    Synchronized block placed around the match and execution processing which is the only
    code that changes the contents of these internal collections.
    It may be possible to improve new order throughput by using synchronized or concurrent 
    collection types and removing the synchronized block.
    
        