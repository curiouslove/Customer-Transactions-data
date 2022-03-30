import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainApplication {
    public static void main(String[] args) {
        Customer customer1 = new Customer(1, "John Wick");

        CustomerTransaction transaction1 = new CustomerTransaction(1, 1, BigDecimal.valueOf(12), LocalDate.of(2022, 1, 19));
        CustomerTransaction transaction2 = new CustomerTransaction(2, 1, BigDecimal.valueOf(10), LocalDate.of(2022, 1, 20));
        CustomerTransaction transaction3 = new CustomerTransaction(3, 1, BigDecimal.valueOf(23), LocalDate.of(2022, 2, 22));

        List<Customer> customerList = List.of(customer1);
        List<CustomerTransaction> transactionList = List.of(transaction1, transaction2, transaction3);

        Map<String, List<CustomerTransaction>> integerListMap = transactionList
                .parallelStream()
                .unordered()
                .collect(Collectors.groupingBy(e -> e.getYearMonth(e.dateCreated())));

        List<CustomerTransactionResponse> transactionResponses = integerListMap.entrySet().parallelStream().unordered().map(e -> {
            CustomerTransactionResponse response = new CustomerTransactionResponse();
            response.setYearMonth(e.getKey());

            BigDecimal amount = e.getValue().parallelStream().unordered()
                    .map(trans -> {
                        response.setName(customerList.stream().filter(c -> c.id() == trans.customerId()).findFirst().get().name());

                        return trans.amount();
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            response.setTotalAmount(amount);
            return response;
        }).toList();

        System.out.println(transactionResponses);

    }
}
