import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/*
 * E-Commerce Order Management System
 * Pure Java Implementation
 *
 * Demonstrates:
 * Classes and Objects
 * Inheritance
 * Polymorphism
 * Method Overloading
 * Interfaces
 * String Handling
 * ArrayList
 * HashSet
 * HashMap
 * Generics
 * Iterators
 * Exception Handling
 * Multithreading
 * Synchronization
 * Thread Priority
 * Inter-thread Communication
 * AWT and Event Handling
 * Java I/O
 * Serialization
 * JDBC
 */

/* =========================
   INTERFACES
   ========================= */

interface PaymentProcessor {
    boolean makePayment(double amount)
            throws InvalidPaymentException;
}

interface OrderProcessor {
    void processOrder()
            throws OrderException;
}

/* =========================
   USER CLASSES
   ========================= */

abstract class User implements Serializable {

    protected int userId;
    protected String name;
    protected String email;

    public User(int userId,
                String name,
                String email) {

        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public abstract String getUserType();

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

/* Inheritance */
class Customer extends User {

    private String address;

    public Customer(int userId,
                    String name,
                    String email,
                    String address) {

        super(userId, name, email);
        this.address = address;
    }

    @Override
    public String getUserType() {
        return "Customer";
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {

        return userId +
                " - " +
                name +
                " - " +
                email;
    }
}

/* Another inherited class */
class Admin extends User {

    public Admin(int userId,
                 String name,
                 String email) {

        super(userId, name, email);
    }

    @Override
    public String getUserType() {
        return "Administrator";
    }
}

/* =========================
   PRODUCT CLASS
   ========================= */

class Product implements Serializable {

    private int productId;
    private String productName;
    private double price;
    private int quantity;

    public Product(int productId,
                   String productName,
                   double price,
                   int quantity) {

        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public synchronized int getQuantity() {
        return quantity;
    }

    /*
     * Synchronization prevents multiple
     * threads from modifying stock
     * at the same time.
     */
    public synchronized void reduceStock(int amount)
            throws InsufficientStockException {

        if (amount <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        if (quantity < amount) {

            throw new InsufficientStockException(
                    "Insufficient stock for " +
                    productName);
        }

        quantity -= amount;

        System.out.println(
                "Inventory update: " +
                productName +
                " stock = " +
                quantity);
    }

    public synchronized void increaseStock(int amount) {

        if (amount > 0) {
            quantity += amount;
        }
    }

    @Override
    public String toString() {

        return productName +
                " | Price: Rs." +
                price +
                " | Stock: " +
                quantity;
    }
}

/* =========================
   EXCEPTIONS
   ========================= */

class OrderException extends Exception {

    public OrderException(String message) {
        super(message);
    }
}

class InsufficientStockException
        extends OrderException {

    public InsufficientStockException(
            String message) {

        super(message);
    }
}

class ProductUnavailableException
        extends OrderException {

    public ProductUnavailableException(
            String message) {

        super(message);
    }
}

class InvalidPaymentException
        extends Exception {

    public InvalidPaymentException(
            String message) {

        super(message);
    }
}

/* =========================
   PAYMENT CLASSES
   ========================= */

abstract class Payment
        implements PaymentProcessor, Serializable {

    protected String transactionId;
    protected double amount;

    public Payment(String transactionId,
                   double amount) {

        this.transactionId = transactionId;
        this.amount = amount;
    }

    public abstract String getPaymentType();
}

/* Card Payment */
class CardPayment extends Payment {

    public CardPayment(String transactionId,
                       double amount) {

        super(transactionId, amount);
    }

    @Override
    public String getPaymentType() {

        return "Card Payment";
    }

    @Override
    public boolean makePayment(double amount)
            throws InvalidPaymentException {

        if (amount <= 0) {

            throw new InvalidPaymentException(
                    "Invalid card payment amount");
        }

        System.out.println(
                "Card payment processed: Rs." +
                amount);

        return true;
    }
}

/* UPI Payment */
class UpiPayment extends Payment {

    public UpiPayment(String transactionId,
                      double amount) {

        super(transactionId, amount);
    }

    /*
     * IMPORTANT:
     * makePayment() must return boolean
     * because PaymentProcessor requires
     * boolean makePayment(double amount).
     */
    @Override
    public boolean makePayment(double amount)
            throws InvalidPaymentException {

        if (amount <= 0) {

            throw new InvalidPaymentException(
                    "Invalid UPI payment amount");
        }

        System.out.println(
                "UPI payment processed: Rs." +
                amount);

        return true;
    }

    @Override
    public String getPaymentType() {

        return "UPI Payment";
    }
}

/* =========================
   ORDER CLASS
   ========================= */

class Order implements Serializable {

    private String orderId;
    private Customer customer;
    private Product product;
    private int quantity;
    private double totalAmount;
    private String status;

    public Order(String orderId,
                 Customer customer,
                 Product product,
                 int quantity) {

        this.orderId = orderId;
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;

        totalAmount =
                calculateTotal(
                        product,
                        quantity);

        status = "CREATED";
    }

    /* Method Overloading - Version 1 */
    public double calculateTotal(
            Product product,
            int quantity) {

        return product.getPrice() *
                quantity;
    }

    /* Method Overloading - Version 2 */
    public double calculateTotal(
            Product product,
            int quantity,
            double discount) {

        double amount =
                product.getPrice() *
                quantity;

        return amount - discount;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {

        return "Order " +
                orderId +
                " | Customer: " +
                customer.getName() +
                " | Product: " +
                product.getProductName() +
                " | Quantity: " +
                quantity +
                " | Amount: Rs." +
                totalAmount +
                " | Status: " +
                status;
    }
}

/* =========================
   INVENTORY MANAGER
   ========================= */

class InventoryManager {

    private HashMap<Integer, Product> products;

    public InventoryManager() {

        products =
                new HashMap<>();
    }

    public void addProduct(Product product) {

        products.put(
                product.getProductId(),
                product);
    }

    public Product getProduct(int productId)
            throws ProductUnavailableException {

        Product product =
                products.get(productId);

        if (product == null) {

            throw new ProductUnavailableException(
                    "Product is unavailable");
        }

        return product;
    }

    public void displayProducts() {

        System.out.println(
                "\nAvailable Products:");

        for (Product product :
                products.values()) {

            System.out.println(product);
        }
    }

    public HashMap<Integer, Product>
            getProducts() {

        return products;
    }
}

/* =========================
   ORDER PROCESSING THREAD
   ========================= */

class OrderTask extends Thread
        implements OrderProcessor {

    private Order order;
    private Payment payment;

    public OrderTask(Order order,
                     Payment payment) {

        this.order = order;
        this.payment = payment;
    }

    @Override
    public void run() {

        try {

            processOrder();

        } catch (OrderException e) {

            order.setStatus("REJECTED");

            System.out.println(
                    "Order " +
                    order.getOrderId() +
                    ": REJECTED - " +
                    e.getMessage());
        }
    }

    @Override
    public void processOrder()
            throws OrderException {

        System.out.println(
                "Processing " +
                order.getOrderId() +
                " for " +
                order.getCustomer().getName());

        /*
         * Reduce stock using synchronized method.
         */
        order.getProduct()
                .reduceStock(
                        order.getQuantity());

        /*
         * Payment processing.
         *
         * InvalidPaymentException is
         * handled here.
         */
        try {

            boolean paymentSuccessful =
                    payment.makePayment(
                            order.getTotalAmount());

            if (!paymentSuccessful) {

                order.getProduct()
                        .increaseStock(
                                order.getQuantity());

                order.setStatus(
                        "PAYMENT FAILED");

                System.out.println(
                        "Payment failed for Order " +
                        order.getOrderId());

                return;
            }

        } catch (InvalidPaymentException e) {

            /*
             * Restore stock if payment fails.
             */
            order.getProduct()
                    .increaseStock(
                            order.getQuantity());

            order.setStatus(
                    "PAYMENT FAILED");

            System.out.println(
                    "Payment failed for Order " +
                    order.getOrderId() +
                    ": " +
                    e.getMessage());

            return;
        }

        order.setStatus("CONFIRMED");

        System.out.println(
                "Order " +
                order.getOrderId() +
                ": PAYMENT SUCCESS");

        /*
         * Inter-thread communication.
         */
        synchronized (this) {

            notifyAll();
        }
    }
}

/* =========================
   SERIALIZATION MANAGER
   ========================= */

class SerializationManager {

    private static final String FILE =
            "orders.dat";

    public static void saveOrders(
            ArrayList<Order> orders) {

        try (
                ObjectOutputStream output =
                        new ObjectOutputStream(
                                new FileOutputStream(
                                        FILE))
        ) {

            output.writeObject(orders);

            System.out.println(
                    "\nSerialized order data saved successfully.");

        } catch (IOException e) {

            System.out.println(
                    "Serialization error: " +
                    e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Order>
            loadOrders() {

        try (
                ObjectInputStream input =
                        new ObjectInputStream(
                                new FileInputStream(
                                        FILE))
        ) {

            return (ArrayList<Order>)
                    input.readObject();

        } catch (Exception e) {

            return new ArrayList<>();
        }
    }
}

/* =========================
   JDBC MANAGER
   ========================= */

class JDBCManager {

    private Connection connection;

    public boolean connect(
            String url,
            String user,
            String password) {

        try {

            connection =
                    DriverManager.getConnection(
                            url,
                            user,
                            password);

            System.out.println(
                    "JDBC database connection established.");

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "JDBC connection unavailable: " +
                    e.getMessage());

            return false;
        }
    }

    /* CREATE */
    public void insertCustomer(
            int id,
            String name,
            String email) {

        if (connection == null) {

            System.out.println(
                    "JDBC connection not available.");

            return;
        }

        String sql =
                "INSERT INTO customers" +
                "(id,name,email) VALUES(?,?,?)";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);
            statement.setString(2, name);
            statement.setString(3, email);

            statement.executeUpdate();

        } catch (SQLException e) {

            System.out.println(
                    "JDBC INSERT error: " +
                    e.getMessage());
        }
    }

    /* UPDATE */
    public void updateCustomer(
            int id,
            String name) {

        if (connection == null) {
            return;
        }

        String sql =
                "UPDATE customers " +
                "SET name=? WHERE id=?";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, name);
            statement.setInt(2, id);

            statement.executeUpdate();

        } catch (SQLException e) {

            System.out.println(
                    "JDBC UPDATE error: " +
                    e.getMessage());
        }
    }

    /* DELETE */
    public void deleteCustomer(int id) {

        if (connection == null) {
            return;
        }

        String sql =
                "DELETE FROM customers " +
                "WHERE id=?";

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            statement.executeUpdate();

        } catch (SQLException e) {

            System.out.println(
                    "JDBC DELETE error: " +
                    e.getMessage());
        }
    }

    /* READ */
    public void retrieveCustomers() {

        if (connection == null) {
            return;
        }

        String sql =
                "SELECT id,name,email " +
                "FROM customers";

        try (
                Statement statement =
                        connection.createStatement();

                ResultSet result =
                        statement.executeQuery(sql)
        ) {

            while (result.next()) {

                System.out.println(
                        result.getInt("id") +
                        " " +
                        result.getString("name") +
                        " " +
                        result.getString("email"));
            }

        } catch (SQLException e) {

            System.out.println(
                    "JDBC retrieval error: " +
                    e.getMessage());
        }
    }
}

/* =========================
   AWT GUI
   ========================= */

class ECommerceGUI extends Frame
        implements ActionListener {

    private TextField productField;
    private TextField quantityField;

    private Button orderButton;
    private Button clearButton;

    private TextArea output;

    public ECommerceGUI() {

        setTitle(
                "E-Commerce Order Management System");

        setSize(600, 400);

        setLayout(
                new BorderLayout());

        MenuBar menuBar =
                new MenuBar();

        Menu menu =
                new Menu("System");

        MenuItem exit =
                new MenuItem("Exit");

        exit.addActionListener(this);

        menu.add(exit);

        menuBar.add(menu);

        setMenuBar(menuBar);

        Panel panel =
                new Panel();

        panel.setLayout(
                new GridLayout(
                        3, 2, 10, 10));

        panel.add(
                new Label("Product ID:"));

        productField =
                new TextField();

        panel.add(productField);

        panel.add(
                new Label("Quantity:"));

        quantityField =
                new TextField();

        panel.add(quantityField);

        orderButton =
                new Button("Place Order");

        clearButton =
                new Button("Clear");

        panel.add(orderButton);

        panel.add(clearButton);

        add(
                panel,
                BorderLayout.NORTH);

        output =
                new TextArea();

        output.setEditable(false);

        add(
                output,
                BorderLayout.CENTER);

        orderButton.addActionListener(this);

        clearButton.addActionListener(this);

        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(
                            WindowEvent e) {

                        dispose();
                    }
                });

        setVisible(true);
    }

    @Override
    public void actionPerformed(
            ActionEvent event) {

        if (event.getActionCommand()
                .equals("Exit")) {

            dispose();

        } else if (
                event.getActionCommand()
                        .equals("Clear")) {

            productField.setText("");

            quantityField.setText("");

            output.setText("");

        } else if (
                event.getActionCommand()
                        .equals("Place Order")) {

            try {

                int productId =
                        Integer.parseInt(
                                productField.getText());

                int quantity =
                        Integer.parseInt(
                                quantityField.getText());

                if (productId <= 0 ||
                        quantity <= 0) {

                    throw new IllegalArgumentException(
                            "Invalid product or quantity");
                }

                output.append(
                        "Order request accepted.\n");

                output.append(
                        "Product ID: " +
                        productId +
                        "\n");

                output.append(
                        "Quantity: " +
                        quantity +
                        "\n");

            } catch (NumberFormatException e) {

                output.append(
                        "Error: Enter numeric values.\n");

            } catch (IllegalArgumentException e) {

                output.append(
                        "Error: " +
                        e.getMessage() +
                        "\n");
            }
        }
    }
}

/* =========================
   MAIN APPLICATION
   ========================= */

public class ECommerceOrderManagement {

    public static void main(String[] args)
            throws Exception {

        System.out.println(
                "======================================");

        System.out.println(
                "   E-COMMERCE ORDER MANAGEMENT SYSTEM");

        System.out.println(
                "======================================");

        /* =========================
           CUSTOMER CREATION
           ========================= */

        Customer alice =
                new Customer(
                        101,
                        "Alice",
                        "alice@email.com",
                        "Chennai");

        Customer bob =
                new Customer(
                        102,
                        "Bob",
                        "bob@email.com",
                        "Chennai");

        Customer carol =
                new Customer(
                        103,
                        "Carol",
                        "carol@email.com",
                        "Chennai");

        /* ArrayList */

        ArrayList<Customer> customers =
                new ArrayList<>();

        customers.add(alice);
        customers.add(bob);
        customers.add(carol);

        System.out.println(
                "\nCustomers loaded: " +
                customers.size());

        /* HashSet */

        HashSet<String> customerEmails =
                new HashSet<>();

        for (Customer customer :
                customers) {

            customerEmails.add(
                    customer.getEmail());
        }

        System.out.println(
                "Unique customer emails: " +
                customerEmails.size());

        /* =========================
           PRODUCT MANAGEMENT
           ========================= */

        Product laptop =
                new Product(
                        1,
                        "Laptop",
                        50000,
                        5);

        Product headphones =
                new Product(
                        2,
                        "Headphones",
                        2000,
                        10);

        InventoryManager inventory =
                new InventoryManager();

        inventory.addProduct(laptop);
        inventory.addProduct(headphones);

        inventory.displayProducts();

        /* Iterator */

        System.out.println(
                "\nCustomer Iterator:");

        Iterator<Customer> iterator =
                customers.iterator();

        while (iterator.hasNext()) {

            Customer customer =
                    iterator.next();

            System.out.println(customer);
        }

        /* =========================
           POLYMORPHISM
           ========================= */

        User user1 = alice;

        User user2 =
                new Admin(
                        501,
                        "System Admin",
                        "admin@email.com");

        System.out.println(
                "\nPolymorphism:");

        System.out.println(
                user1.getName() +
                " -> " +
                user1.getUserType());

        System.out.println(
                user2.getName() +
                " -> " +
                user2.getUserType());

        /* =========================
           ORDERS
           ========================= */

        Order order1 =
                new Order(
                        "O1001",
                        alice,
                        laptop,
                        1);

        Order order2 =
                new Order(
                        "O1002",
                        bob,
                        laptop,
                        1);

        Order order3 =
                new Order(
                        "O1003",
                        carol,
                        laptop,
                        4);

        ArrayList<Order> orders =
                new ArrayList<>();

        orders.add(order1);
        orders.add(order2);
        orders.add(order3);

        /* HashMap */

        HashMap<String, Order> orderMap =
                new HashMap<>();

        for (Order order : orders) {

            orderMap.put(
                    order.getOrderId(),
                    order);
        }

        System.out.println(
                "\nOrders created: " +
                orderMap.size());

        /* =========================
           PAYMENT
           ========================= */

        Payment payment1 =
                new CardPayment(
                        "T1001",
                        order1.getTotalAmount());

        Payment payment2 =
                new UpiPayment(
                        "T1002",
                        order2.getTotalAmount());

        Payment payment3 =
                new CardPayment(
                        "T1003",
                        order3.getTotalAmount());

        /* =========================
           MULTITHREADING
           ========================= */

        System.out.println(
                "\nOrder processing started " +
                "with concurrent threads.");

        OrderTask thread1 =
                new OrderTask(
                        order1,
                        payment1);

        OrderTask thread2 =
                new OrderTask(
                        order2,
                        payment2);

        OrderTask thread3 =
                new OrderTask(
                        order3,
                        payment3);

        /* Thread priorities */

        thread1.setPriority(
                Thread.MAX_PRIORITY);

        thread2.setPriority(
                Thread.NORM_PRIORITY);

        thread3.setPriority(
                Thread.MIN_PRIORITY);

        /* Start threads */

        thread1.start();
        thread2.start();
        thread3.start();

        /* Wait for threads */

        thread1.join();
        thread2.join();
        thread3.join();

        /* =========================
           FINAL INVENTORY
           ========================= */

        System.out.println(
                "\nFinal Inventory:");

        inventory.displayProducts();

        /* =========================
           ORDER RESULTS
           ========================= */

        System.out.println(
                "\nFinal Order Status:");

        for (Order order : orders) {

            System.out.println(order);
        }

        /* =========================
           SERIALIZATION
           ========================= */

        SerializationManager.saveOrders(
                orders);

        ArrayList<Order> loadedOrders =
                SerializationManager.loadOrders();

        System.out.println(
                "Orders retrieved from serialized file: " +
                loadedOrders.size());

        /* =========================
           STRING HANDLING
           ========================= */

        String customerName =
                alice.getName();

        System.out.println(
                "\nString Handling:");

        System.out.println(
                "Customer name: " +
                customerName);

        System.out.println(
                "Uppercase: " +
                customerName.toUpperCase());

        System.out.println(
                "Name length: " +
                customerName.length());

        /* =========================
           VALIDATION
           ========================= */

        System.out.println(
                "\nValidation completed: PASS");

        System.out.println(
                "Concurrent inventory updates " +
                "were synchronized.");

        System.out.println(
                "System execution completed successfully.");

        /*
         * AWT GUI
         *
         * To launch GUI manually:
         *
         * ECommerceGUI gui =
         *      new ECommerceGUI();
         */
    }
}
