package foodorder.management;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class foodimp extends Frame implements ActionListener {

    static final String URL = "jdbc:mysql://localhost:3306/food_order_db";
    static final String USER = "root";
    static final String PASSWORD = "root";

    CardLayout card;
    Panel mainPanel;

    // Colors
    Color dark = new Color(45, 55, 72);
    Color blue = new Color(37, 99, 235);
    Color green = new Color(22, 163, 74);
    Color red = new Color(220, 38, 38);
    Color light = new Color(241, 245, 249);
    Color white = Color.WHITE;

    Font titleFont = new Font("Arial", Font.BOLD, 24);
    Font buttonFont = new Font("Arial", Font.BOLD, 14);

    // Constructor
    foodimp() {

        setTitle("Online Food Order Management System");
        setSize(1100, 700);
        setLayout(new BorderLayout());
        setBackground(light);

        // Header
        Panel header = new Panel();
        header.setBackground(dark);

        Label title = new Label(
                "ONLINE FOOD ORDER MANAGEMENT SYSTEM",
                Label.CENTER
        );

        title.setFont(titleFont);
        title.setForeground(Color.WHITE);

        header.add(title);
        add(header, BorderLayout.NORTH);

        // Left menu
        Panel menu = new Panel();
        menu.setLayout(new GridLayout(11, 1, 5, 5));
        menu.setBackground(dark);

        String[] buttons = {
                "Register Customer",
                "View Customers",
                "Add Food Item",
                "View Available Food",
                "Search Food",
                "Place Order",
                "Customer Orders",
                "Update Order Status",
                "Cancel Order",
                "Order Details",
                "Exit"
        };

        for (String text : buttons) {
            Button b = new Button(text);
            b.setFont(buttonFont);
            b.setBackground(blue);
            b.setForeground(Color.WHITE);
            b.addActionListener(this);
            menu.add(b);
        }

        add(menu, BorderLayout.WEST);

        // Main area
        card = new CardLayout();
        mainPanel = new Panel(card);
        mainPanel.setBackground(light);

        createHomePanel();
        createRegisterPanel();
        createCustomerViewPanel();
        createFoodPanel();
        createAvailableFoodPanel();
        createSearchFoodPanel();
        createOrderPanel();
        createCustomerOrdersPanel();
        createStatusPanel();
        createCancelPanel();
        createOrderDetailsPanel();

        add(mainPanel, BorderLayout.CENTER);

        // Close window
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        setVisible(true);
    }

    // Database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ---------- HOME ----------
    void createHomePanel() {

        Panel p = new Panel(new BorderLayout());
        p.setBackground(light);

        Label l = new Label(
                "Welcome to Online Food Order Management",
                Label.CENTER
        );

        l.setFont(new Font("Arial", Font.BOLD, 30));
        l.setForeground(dark);

        p.add(l, BorderLayout.CENTER);

        mainPanel.add(p, "Home");
    }

    // ---------- REGISTER CUSTOMER ----------
    void createRegisterPanel() {

        Panel p = createPanel();

        Label title = createTitle("REGISTER CUSTOMER");

        TextField name = new TextField();
        TextField phone = new TextField();
        TextField email = new TextField();
        TextField address = new TextField();

        Button register = createButton("Register Customer", green);

        addField(p, title, "Customer Name:", name);
        addField(p, null, "Phone:", phone);
        addField(p, null, "Email:", email);
        addField(p, null, "Address:", address);

        Panel bp = new Panel();
        bp.add(register);

        p.add(bp);

        register.addActionListener(e -> {

            try (Connection con = getConnection()) {

                String sql =
                        "INSERT INTO customers(name,phone,email,address) " +
                        "VALUES(?,?,?,?)";

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ps.setString(1, name.getText());
                ps.setString(2, phone.getText());
                ps.setString(3, email.getText());
                ps.setString(4, address.getText());

                ps.executeUpdate();

                showMessage("Customer registered successfully!");

                name.setText("");
                phone.setText("");
                email.setText("");
                address.setText("");

            } catch (SQLException ex) {
                showMessage("Error: " + ex.getMessage());
            }
        });

        mainPanel.add(p, "Register");
    }

    // ---------- VIEW CUSTOMERS ----------
    void createCustomerViewPanel() {

        Panel p = createPanel();

        Label title = createTitle("CUSTOMER DETAILS");

        TextArea area = new TextArea();
        area.setFont(new Font("Arial", Font.PLAIN, 14));

        Button view = createButton("View Customers", blue);

        p.add(title);
        p.add(area);

        Panel bp = new Panel();
        bp.add(view);
        p.add(bp);

        view.addActionListener(e -> {

            area.setText("");

            String sql = "SELECT * FROM customers";

            try (Connection con = getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {

                    area.append(
                            "ID: " + rs.getInt("customer_id") +
                            "    Name: " + rs.getString("name") +
                            "    Phone: " + rs.getString("phone") +
                            "    Email: " + rs.getString("email") +
                            "    Address: " + rs.getString("address") +
                            "\n\n"
                    );
                }

            } catch (SQLException ex) {
                showMessage("Error: " + ex.getMessage());
            }
        });

        mainPanel.add(p, "Customers");
    }

    // ---------- ADD FOOD ----------
    void createFoodPanel() {

        Panel p = createPanel();

        Label title = createTitle("ADD FOOD ITEM");

        TextField name = new TextField();
        TextField category = new TextField();
        TextField price = new TextField();

        Button add = createButton("Add Food", green);

        addField(p, title, "Food Name:", name);
        addField(p, null, "Category:", category);
        addField(p, null, "Price:", price);

        Panel bp = new Panel();
        bp.add(add);
        p.add(bp);

        add.addActionListener(e -> {

            try {

                double amount =
                        Double.parseDouble(price.getText());

                Connection con = getConnection();

                String sql =
                        "INSERT INTO food_items " +
                        "(food_name,category,price,availability) " +
                        "VALUES(?,?,?,TRUE)";

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ps.setString(1, name.getText());
                ps.setString(2, category.getText());
                ps.setDouble(3, amount);

                ps.executeUpdate();

                con.close();

                showMessage("Food item added successfully!");

                name.setText("");
                category.setText("");
                price.setText("");

            } catch (Exception ex) {
                showMessage("Error: " + ex.getMessage());
            }
        });

        mainPanel.add(p, "AddFood");
    }

    // ---------- AVAILABLE FOOD ----------
    void createAvailableFoodPanel() {

        Panel p = createPanel();

        Label title = createTitle("AVAILABLE FOOD ITEMS");

        TextArea area = new TextArea();

        Button view = createButton("View Food", blue);

        p.add(title);
        p.add(area);

        Panel bp = new Panel();
        bp.add(view);
        p.add(bp);

        view.addActionListener(e -> {

            area.setText("");

            String sql =
                    "SELECT * FROM food_items " +
                    "WHERE availability=TRUE";

            try (Connection con = getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {

                    area.append(
                            "Food ID: " + rs.getInt("food_id") +
                            "    Name: " + rs.getString("food_name") +
                            "    Category: " + rs.getString("category") +
                            "    Price: ₹" + rs.getDouble("price") +
                            "\n\n"
                    );
                }

            } catch (SQLException ex) {
                showMessage("Error: " + ex.getMessage());
            }
        });

        mainPanel.add(p, "AvailableFood");
    }

    // ---------- SEARCH FOOD ----------
    void createSearchFoodPanel() {

        Panel p = createPanel();

        Label title = createTitle("SEARCH FOOD BY CATEGORY");

        TextField category = new TextField();
        TextArea result = new TextArea();

        Button search = createButton("Search", blue);

        addField(p, title, "Category:", category);

        p.add(result);

        Panel bp = new Panel();
        bp.add(search);
        p.add(bp);

        search.addActionListener(e -> {

            result.setText("");

            try (Connection con = getConnection()) {

                String sql =
                        "SELECT * FROM food_items " +
                        "WHERE category=? AND availability=TRUE";

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ps.setString(1, category.getText());

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {

                    result.append(
                            "ID: " + rs.getInt("food_id") +
                            "    " + rs.getString("food_name") +
                            "    ₹" + rs.getDouble("price") +
                            "\n\n"
                    );
                }

            } catch (SQLException ex) {
                showMessage("Error: " + ex.getMessage());
            }
        });

        mainPanel.add(p, "SearchFood");
    }

    // ---------- PLACE ORDER ----------
    void createOrderPanel() {

        Panel p = createPanel();

        Label title = createTitle("PLACE FOOD ORDER");

        TextField customerId = new TextField();
        TextField foodId = new TextField();
        TextField quantity = new TextField();

        Button order = createButton("Place Order", green);

        addField(p, title, "Customer ID:", customerId);
        addField(p, null, "Food ID:", foodId);
        addField(p, null, "Quantity:", quantity);

        Panel bp = new Panel();
        bp.add(order);
        p.add(bp);

        order.addActionListener(e -> {

            try (Connection con = getConnection()) {

                int cid = Integer.parseInt(customerId.getText());
                int fid = Integer.parseInt(foodId.getText());
                int qty = Integer.parseInt(quantity.getText());

                String foodSQL =
                        "SELECT price FROM food_items " +
                        "WHERE food_id=? AND availability=TRUE";

                PreparedStatement foodPS =
                        con.prepareStatement(foodSQL);

                foodPS.setInt(1, fid);

                ResultSet rs = foodPS.executeQuery();

                if (!rs.next()) {
                    showMessage("Food item not available.");
                    return;
                }

                double price = rs.getDouble("price");
                double subtotal = price * qty;

                String orderSQL =
                        "INSERT INTO orders" +
                        "(customer_id,total_amount,status) " +
                        "VALUES(?,?,?)";

                PreparedStatement orderPS =
                        con.prepareStatement(
                                orderSQL,
                                Statement.RETURN_GENERATED_KEYS
                        );

                orderPS.setInt(1, cid);
                orderPS.setDouble(2, subtotal);
                orderPS.setString(3, "Placed");

                orderPS.executeUpdate();

                ResultSet keys =
                        orderPS.getGeneratedKeys();

                int orderId = 0;

                if (keys.next()) {
                    orderId = keys.getInt(1);
                }

                String itemSQL =
                        "INSERT INTO order_items" +
                        "(order_id,food_id,quantity,subtotal)" +
                        " VALUES(?,?,?,?)";

                PreparedStatement itemPS =
                        con.prepareStatement(itemSQL);

                itemPS.setInt(1, orderId);
                itemPS.setInt(2, fid);
                itemPS.setInt(3, qty);
                itemPS.setDouble(4, subtotal);

                itemPS.executeUpdate();

                showMessage(
                        "Order placed successfully!\n" +
                        "Order ID: " + orderId +
                        "\nTotal Amount: ₹" + subtotal
                );

            } catch (Exception ex) {
                showMessage("Error: " + ex.getMessage());
            }
        });

        mainPanel.add(p, "PlaceOrder");
    }

    // ---------- CUSTOMER ORDERS ----------
    void createCustomerOrdersPanel() {

        Panel p = createPanel();

        Label title = createTitle("CUSTOMER ORDERS");

        TextField customerId = new TextField();
        TextArea result = new TextArea();

        Button view = createButton("View Orders", blue);

        addField(p, title, "Customer ID:", customerId);

        p.add(result);

        Panel bp = new Panel();
        bp.add(view);
        p.add(bp);

        view.addActionListener(e -> {

            result.setText("");

            try (Connection con = getConnection()) {

                int cid =
                        Integer.parseInt(customerId.getText());

                String sql =
                        "SELECT * FROM orders " +
                        "WHERE customer_id=?";

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ps.setInt(1, cid);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {

                    result.append(
                            "Order ID: " +
                            rs.getInt("order_id") +
                            "\nDate: " +
                            rs.getTimestamp("order_date") +
                            "\nAmount: ₹" +
                            rs.getDouble("total_amount") +
                            "\nStatus: " +
                            rs.getString("status") +
                            "\n\n"
                    );
                }

            } catch (Exception ex) {
                showMessage("Error: " + ex.getMessage());
            }
        });

        mainPanel.add(p, "CustomerOrders");
    }

    // ---------- UPDATE STATUS ----------
    void createStatusPanel() {

        Panel p = createPanel();

        Label title = createTitle("UPDATE ORDER STATUS");

        TextField orderId = new TextField();

        Choice status = new Choice();

        status.add("Placed");
        status.add("Preparing");
        status.add("Out for Delivery");
        status.add("Delivered");

        Button update = createButton("Update Status", green);

        addField(p, title, "Order ID:", orderId);

        Panel cp = new Panel(new GridLayout(1, 2, 10, 10));

        cp.add(new Label("Select Status:"));
        cp.add(status);

        p.add(cp);

        Panel bp = new Panel();
        bp.add(update);
        p.add(bp);

        update.addActionListener(e -> {

            try (Connection con = getConnection()) {

                int oid =
                        Integer.parseInt(orderId.getText());

                String sql =
                        "UPDATE orders SET status=? " +
                        "WHERE order_id=?";

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ps.setString(1, status.getSelectedItem());
                ps.setInt(2, oid);

                int rows = ps.executeUpdate();

                if (rows > 0)
                    showMessage("Order status updated!");
                else
                    showMessage("Order not found.");

            } catch (Exception ex) {
                showMessage("Error: " + ex.getMessage());
            }
        });

        mainPanel.add(p, "Status");
    }

    // ---------- CANCEL ORDER ----------
    void createCancelPanel() {

        Panel p = createPanel();

        Label title = createTitle("CANCEL ORDER");

        TextField orderId = new TextField();

        Button cancel =
                createButton("Cancel Order", red);

        addField(p, title, "Order ID:", orderId);

        Panel bp = new Panel();
        bp.add(cancel);
        p.add(bp);

        cancel.addActionListener(e -> {

            try (Connection con = getConnection()) {

                int oid =
                        Integer.parseInt(orderId.getText());

                String sql =
                        "UPDATE orders SET status='Cancelled' " +
                        "WHERE order_id=?";

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ps.setInt(1, oid);

                int rows = ps.executeUpdate();

                if (rows > 0)
                    showMessage("Order cancelled successfully!");
                else
                    showMessage("Order not found.");

            } catch (Exception ex) {
                showMessage("Error: " + ex.getMessage());
            }
        });

        mainPanel.add(p, "Cancel");
    }

    // ---------- ORDER DETAILS ----------
    void createOrderDetailsPanel() {

        Panel p = createPanel();

        Label title = createTitle("ORDER DETAILS");

        TextField orderId = new TextField();
        TextArea result = new TextArea();

        Button view =
                createButton("View Details", blue);

        addField(p, title, "Order ID:", orderId);

        p.add(result);

        Panel bp = new Panel();
        bp.add(view);
        p.add(bp);

        view.addActionListener(e -> {

            result.setText("");

            try (Connection con = getConnection()) {

                int oid =
                        Integer.parseInt(orderId.getText());

                String sql =
                        "SELECT o.order_id,c.name,f.food_name," +
                        "oi.quantity,oi.subtotal,o.total_amount," +
                        "o.status " +
                        "FROM orders o " +
                        "JOIN customers c " +
                        "ON o.customer_id=c.customer_id " +
                        "JOIN order_items oi " +
                        "ON o.order_id=oi.order_id " +
                        "JOIN food_items f " +
                        "ON oi.food_id=f.food_id " +
                        "WHERE o.order_id=?";

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ps.setInt(1, oid);

                ResultSet rs =
                        ps.executeQuery();

                if (rs.next()) {

                    result.append(
                            "Order ID: " +
                            rs.getInt("order_id") +
                            "\n\nCustomer: " +
                            rs.getString("name") +
                            "\n\nFood: " +
                            rs.getString("food_name") +
                            "\n\nQuantity: " +
                            rs.getInt("quantity") +
                            "\n\nSubtotal: ₹" +
                            rs.getDouble("subtotal") +
                            "\n\nTotal: ₹" +
                            rs.getDouble("total_amount") +
                            "\n\nStatus: " +
                            rs.getString("status")
                    );

                } else {
                    result.setText("Order not found.");
                }

            } catch (Exception ex) {
                showMessage("Error: " + ex.getMessage());
            }
        });

        mainPanel.add(p, "OrderDetails");
    }

    // ---------- COMMON METHODS ----------

    Panel createPanel() {

        Panel p = new Panel(new GridLayout(0, 1, 15, 15));
        p.setBackground(light);
        p.setFont(new Font("Arial", Font.PLAIN, 15));

        return p;
    }

    Label createTitle(String text) {

        Label l =
                new Label(text, Label.CENTER);

        l.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        l.setForeground(dark);

        return l;
    }

    Button createButton(String text, Color color) {

        Button b = new Button(text);

        b.setFont(buttonFont);
        b.setBackground(color);
        b.setForeground(Color.WHITE);

        return b;
    }

    void addField(
            Panel p,
            Component title,
            String label,
            TextField field) {

        if (title != null)
            p.add(title);

        Panel row =
                new Panel(new GridLayout(1, 2, 10, 10));

        row.add(new Label(label));
        row.add(field);

        p.add(row);
    }

    void showMessage(String message) {

        Dialog d =
                new Dialog(this, "Message", true);

        d.setLayout(new BorderLayout());
        d.setSize(400, 180);

        Label l =
                new Label(message, Label.CENTER);

        l.setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        Button ok = new Button("OK");

        ok.addActionListener(e -> d.dispose());

        d.add(l, BorderLayout.CENTER);

        Panel bp = new Panel();
        bp.add(ok);

        d.add(bp, BorderLayout.SOUTH);

        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    // ---------- BUTTON ACTIONS ----------

    public void actionPerformed(ActionEvent e) {

        String command =
                ((Button)e.getSource()).getLabel();

        switch (command) {

            case "Register Customer":
                card.show(mainPanel, "Register");
                break;

            case "View Customers":
                card.show(mainPanel, "Customers");
                break;

            case "Add Food Item":
                card.show(mainPanel, "AddFood");
                break;

            case "View Available Food":
                card.show(mainPanel, "AvailableFood");
                break;

            case "Search Food":
                card.show(mainPanel, "SearchFood");
                break;

            case "Place Order":
                card.show(mainPanel, "PlaceOrder");
                break;

            case "Customer Orders":
                card.show(mainPanel, "CustomerOrders");
                break;

            case "Update Order Status":
                card.show(mainPanel, "Status");
                break;

            case "Cancel Order":
                card.show(mainPanel, "Cancel");
                break;

            case "Order Details":
                card.show(mainPanel, "OrderDetails");
                break;

            case "Exit":
                dispose();
                System.exit(0);
                break;
        }
    }

    public static void main(String[] args) {

        new foodimp();
    }
}


