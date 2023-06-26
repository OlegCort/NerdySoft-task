package org.example;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class Main {


    public static void main(String[] args) {

        //TODO Create User class with method createUser
        // User class fields: name, age;
        // Notice that we can only create user with createUser method without using constructor or builder
        User user1 = User.createUser("Alice", 32);
        User user2 = User.createUser("Bob", 19);
        User user3 = User.createUser("Charlie", 20);
        User user4 = User.createUser("John", 27);

        //TODO Create factory that can create a product for a specific type: Real or Virtual
        // Product class fields: name, price
        // Product Real class additional fields: size, weight
        // Product Virtual class additional fields: code, expiration date

        Product realProduct1 = ProductFactory.createRealProduct("Product A", 20.50, 10, 25);
        Product realProduct2 = ProductFactory.createRealProduct("Product B", 50, 6, 17);

        Product virtualProduct1 = ProductFactory.createVirtualProduct("Product C", 100, "xxx", LocalDate.of(2023, 5, 12));
        Product virtualProduct2 = ProductFactory.createVirtualProduct("Product D", 81.25, "yyy",  LocalDate.of(2024, 6, 20));


        //TODO Create Order class with method createOrder
        // Order class fields: User, List<Price> // Price? //
        // Notice that we can only create order with createOrder method without using constructor or builder
        List<Order> orders = new ArrayList<>() {{
            add(Order.createOrder(user1, List.of(realProduct1, virtualProduct1, virtualProduct2)));
            add(Order.createOrder(user2, List.of(realProduct1, realProduct2)));
            add(Order.createOrder(user3, List.of(realProduct1, virtualProduct2)));
            add(Order.createOrder(user4, List.of(virtualProduct1, virtualProduct2, realProduct1, realProduct2)));
        }};

        //TODO 1). Create singleton class which will check the code is used already or not
        // Singleton class should have the possibility to mark code as used and check if code used
        // Example:
        // singletonClass.useCode("xxx")
        // boolean isCodeUsed = virtualProductCodeManager.isCodeUsed("xxx") --> true;
        // boolean isCodeUsed = virtualProductCodeManager.isCodeUsed("yyy") --> false;
        System.out.println("1. Create singleton class VirtualProductCodeManager \n");
        var isUsed = new VirtualProductCodeManager().isCodeUsed("xxx");
        System.out.println("Is code used: " + isUsed + "\n");

        //TODO 2). Create a functionality to get the most expensive ordered product
        Product mostExpensive = getMostExpensiveProduct(orders);
        System.out.println("2. Most expensive product: " + mostExpensive + "\n");

        //TODO 3). Create a functionality to get the most popular product(product bought by most users) among users
        Product mostPopular = getMostPopularProduct(orders);
        System.out.println("3. Most popular product: " + mostPopular + "\n");

        //TODO 4). Create a functionality to get average age of users who bought realProduct2
        double averageAge = calculateAverageAge(realProduct2, orders);
        System.out.println("4. Average age is: " + averageAge + "\n");

        //TODO 5). Create a functionality to return map with products as keys and a list of users
        // who ordered each product as values
        Map<Product, List<User>> productUserMap = getProductUserMap(orders);
        System.out.println("5. Map with products as keys and list of users as value \n");
        productUserMap.forEach((key, value) -> System.out.println("key: " + key + " " + "value: " +  value + "\n"));

        //TODO 6). Create a functionality to sort/group entities:
        // a) Sort Products by price
        // b) Sort Orders by user age in descending order
        List<Product> productsByPrice = sortProductsByPrice(List.of(realProduct1, realProduct2, virtualProduct1, virtualProduct2));
        System.out.println("6. a) List of products sorted by price: " + productsByPrice + "\n");
        List<Order> ordersByUserAgeDesc = sortOrdersByUserAgeDesc(orders);
        System.out.println("6. b) List of orders sorted by user agge in descending order: " + ordersByUserAgeDesc + "\n");

        //TODO 7). Calculate the total weight of each order
        Map<Order, Integer> result = calculateWeightOfEachOrder(orders);
        System.out.println("7. Calculate the total weight of each order \n");
        result.forEach((key, value) -> System.out.println("order: " + key + " " + "total weight: " +  value + "\n"));
    }

    private static Product getMostExpensiveProduct(List<Order> orders) {
        if(orders==null)
            throw new IllegalArgumentException("Orders cannot be null");
        return orders.stream().map(Order::getProductList).flatMap(List::stream).sorted((x, y)->Double.compare(y.price,x.price)).toList().get(0);
    }

    private static Product getMostPopularProduct(List<Order> orders) {
        if(orders==null)
            throw new IllegalArgumentException("Parameter order cannot be null");

        Map<Product, Integer> counter = new HashMap<>();
        orders.stream().map(Order::getProductList).flatMap(List::stream)
                .forEach(x->counter.put(x, counter.getOrDefault(x, 0) + 1));
        Product product = null;
        for (Product key : counter.keySet()){
            if(product==null)
                product = key;
            else
                product = counter.get(product)>counter.get(key)?product:key;
        }
        return product;
    }

    private static double calculateAverageAge(Product product, List<Order> orders) {
        if(orders==null)
            throw new IllegalArgumentException("Parameter order cannot be null");
        if(product==null)
            throw new IllegalArgumentException("Parameter product cannot be null");

        int counter = 0;
        int summaryAge = 0;
        for(Order order : orders){
            if(order.getProductList().stream().anyMatch(x->x==product))
            {
                summaryAge += order.getUser().getAge();
                counter++;
            }
        }
        if(counter==0) return 0;
        return summaryAge/(double) counter;
    }

    private static Map<Product, List<User>> getProductUserMap(List<Order> orders) {
        if(orders==null)
            throw new IllegalArgumentException("Parameter order cannot be null");

        Map<Product, List<User>> map = new HashMap<>();
        for(Order order: orders){
            for(Product product: order.getProductList()){
                if(!map.containsKey(product))
                    map.put(product, new ArrayList<>());
                map.get(product).add(order.getUser());
            }
        }
        return map;
    }

    private static List<Product> sortProductsByPrice(List<Product> products) {
        if(products==null)
            throw new IllegalArgumentException("Parameter products cannot be null");
        //return products.stream().sorted((x, y)->Double.compare(y.price,x.price)).toList(); //desc
        return products.stream().sorted(Comparator.comparingDouble(x -> x.price)).toList(); //asc
    }

    private static List<Order> sortOrdersByUserAgeDesc(List<Order> orders) {
        if(orders==null)
            throw new IllegalArgumentException("Parameter orders cannot be null");
        return orders.stream().sorted((x, y)->(y.getUser().getAge()-x.getUser().getAge())).toList();
    }

    private static Map<Order, Integer> calculateWeightOfEachOrder(List<Order> orders) {
        if(orders==null)
            throw new IllegalArgumentException("Parameter orders cannot be null");
        Map<Order, Integer> map = new HashMap<>();
        for(Order order: orders){
            map.put(order, 0);
            order.getProductList().stream().filter(x->x instanceof RealProduct).forEach(x->map.put(order, map.get(order)+((RealProduct)x).getWeight()));
        }
        //return new HashMap<>(Map.of(new Order.OrderBuilder().build(), 0));
        return map;
    }
}


class User{

    //Considering that we cannot use builders, there are no setters, so fields are final
    private static final int MIN_AGE = 18;
    private final String name;
    private final int age;

    private User(String name, int age){
        this.age = age;
        this.name = name;
    }

    public static User createUser(String name, int age){
        if(name==null||name.isEmpty())
            throw new IllegalArgumentException("Name cannot be blank");
        if(age<MIN_AGE)
            throw new IllegalArgumentException("User cannot be younger than 18 y.o.");
        return new User(name, age);
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString(){
        return name;
    }

}

abstract class Product{
    protected String name;
    protected double price;
    public Product(String name, double price){

        if(name==null||name.isEmpty())
            throw new IllegalArgumentException("Name cannot be blank");
        if(price<0)
            throw new IllegalArgumentException("Price cannot be less than 0");
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString(){
        return name;
    }
}

class RealProduct extends Product{

    private final int size;
    private final int weight;

    public RealProduct(String name, double price, int size, int weight){
        super(name, price);
        if(size<=0)
            throw new IllegalArgumentException("Size must be grater than 0.");
        if(weight<=0)
            throw new IllegalArgumentException("Weight must be grater than 0.");
        this.size = size;
        this.weight = weight;
    }

    public int getSize() {
        return size;
    }

    public int getWeight() {
        return weight;
    }
}

class VirtualProduct extends Product{

    private final String code;
    private final LocalDate expirationDate;

    public VirtualProduct(String name, double price, String code, LocalDate expirationDate){
        super(name, price);
        if(code==null || name.isEmpty())
            throw new IllegalArgumentException("Code cannot be blank.");
        if(expirationDate==null)
            throw new IllegalArgumentException("Expiration Date cannot be null.");

        if(SingletonClass.getInstance().isCodeUsed(code))
            throw new IllegalArgumentException("Code <" + code + "> is already used.");

        SingletonClass.getInstance().useCode(code);
        this.code = code;
        this.expirationDate = expirationDate;
    }

    public String getCode() {
        return code;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }
}


class ProductFactory{

    //Methods have different names in example. If names were equal, I would rather use interface or Abstract Factory Pattern, cause than RealProduct's and VirtualProduct's constructors could be private.
    //Constructors of VirtualProduct and RealProduct are public, so validation of parameters are in them
    static RealProduct createRealProduct(String name, double price, int size, int weight){
        return new RealProduct(name, price, size, weight);
    }

    static VirtualProduct createVirtualProduct(String name, double price, String code, LocalDate expirationDate){
        return new VirtualProduct(name, price, code, expirationDate);
    }
}

class Order{

    private final User user;
    private final List<Product> productList; //List final, so it is impossible to modify it.

    private Order(User user, List<Product> productList){
        if(user==null)
            throw new IllegalArgumentException("Owner of order (user) cannot be null");
        if(productList==null||productList.size()==0)
            throw new IllegalArgumentException("Product list cannot be null or empty.");
        this.user = user;
        this.productList = productList;
    }

    public static Order createOrder(User user, List<Product> productList){
        return new Order(user, productList);
    }

    public User getUser() {
        return user;
    }

    public List<Product> getProductList() {
        return new ArrayList<>(productList); //copy of List: maintaining encapsulation
    }


    @Override
    public String toString(){
        return getUser().toString() + "'s order";
    }
}

class SingletonClass{

    private static SingletonClass instance = null;
    private VirtualProductCodeManager vProductCodeManager;

    private SingletonClass(){
        vProductCodeManager = new VirtualProductCodeManager();
    }

    public static SingletonClass getInstance(){
        if(instance==null)
            return new SingletonClass();
        else return instance;
    }

    public void useCode(String code){
        vProductCodeManager.addCode(code);
    }

    public boolean isCodeUsed(String code){
        return vProductCodeManager.isCodeUsed(code);
    }


}

class VirtualProductCodeManager{

    //Manager classes usually use DB to get data, but in my case it will be simple static List of codes
    private static ArrayList<String> codes;

    static {
        codes = new ArrayList<>();
    }

    public void addCode(String code){
        if(code==null||Objects.equals(code, "")) //not going to happen if this method will be used only from VirtualProduct Constructor
            return;
        codes.add(code);
    }

    public boolean isCodeUsed(String code){
        if(code==null||Objects.equals(code, ""))
            throw new IllegalArgumentException("Code to check cannot be null or empty.");
        return codes.contains(code);
    }
}