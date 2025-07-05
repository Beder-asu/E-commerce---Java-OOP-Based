package services;

import models.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class DBManager {
    private static final String DB_URL = "jdbc:sqlite:C:\\Users\\mobed\\Desktop\\Fawry Intern - E commerce\\resources\\ecommerce.db";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // --- CUSTOMER 
    public void saveCustomer(Customer customer) {
        String sql = """
            INSERT INTO customers (id, name, email, balance)
            VALUES (?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                name=excluded.name,
                email=excluded.email,
                balance=excluded.balance
        """;
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getId());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setDouble(4, customer.getBalance());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save customer", e);
        }
    }

    public Customer getCustomer(String id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Customer c = new Customer(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("email")
                );
                c.addFunds(rs.getDouble("balance"));
                return c;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch customer", e);
        }
    }

    public void deleteCustomer(String id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete customer", e);
        }
    }

    // --- PRODUCT
    public void saveProduct(Product product) {
        String sql = """
            INSERT INTO products (id, name, price, quantity, can_expire, expiration_date, weight, type)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                name=excluded.name,
                price=excluded.price,
                quantity=excluded.quantity,
                can_expire=excluded.can_expire,
                expiration_date=excluded.expiration_date,
                weight=excluded.weight,
                type=excluded.type
        """;
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, product.getId());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setDouble(4, product.getQuantity());
            pstmt.setBoolean(5, product.canExpire());
            pstmt.setObject(6, product.canExpire() ? product.getExpirationDate() : null);
            if (product instanceof ShippableProduct) {
                pstmt.setDouble(7, ((ShippableProduct) product).getWeight());
                pstmt.setString(8, "shippable");
            } else {
                pstmt.setObject(7, null);
                pstmt.setString(8, "non-shippable");
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save product", e);
        }
    }

    public Product getProduct(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String type = rs.getString("type");
                if ("shippable".equals(type)) {
                    return new ShippableProduct(
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getDouble("quantity"),
                        rs.getBoolean("can_expire"),
                        rs.getObject("expiration_date") != null ? LocalDate.parse(rs.getString("expiration_date")) : null,
                        rs.getDouble("weight")
                    );
                } else {
                    return new NonShippableProduct(
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getDouble("quantity")
                    );
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch product", e);
        }
    }

    public void deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete product", e);
        }
    }

    // --- CART
    public int createCartForCustomer(String customerId) {
        String insertCart = "INSERT INTO carts DEFAULT VALUES";
        String insertMapping = "INSERT OR REPLACE INTO customer_carts (customer_id, cart_id) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement cartStmt = conn.prepareStatement(insertCart, Statement.RETURN_GENERATED_KEYS)) {
            cartStmt.executeUpdate();
            ResultSet rs = cartStmt.getGeneratedKeys();
            if (rs.next()) {
                int cartId = rs.getInt(1);
                try (PreparedStatement mappingStmt = conn.prepareStatement(insertMapping)) {
                    mappingStmt.setString(1, customerId);
                    mappingStmt.setInt(2, cartId);
                    mappingStmt.executeUpdate();
                }
                return cartId;
            } else {
                throw new SQLException("Failed to create cart");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create cart for customer", e);
        }
    }

    public Map<Product, Integer> getCartContents(int cartId) {
        String sql = "SELECT product_id, quantity FROM cart_items WHERE cart_id = ?";
        Map<Product, Integer> contents = new HashMap<>();
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cartId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Product p = getProduct(rs.getInt("product_id"));
                contents.put(p, rs.getInt("quantity"));
            }
            return contents;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch cart contents", e);
        }
    }

    public void addProductToCart(int cartId, int productId, int quantity) {
        String sql = """
            INSERT INTO cart_items (cart_id, product_id, quantity)
            VALUES (?, ?, ?)
            ON CONFLICT(cart_id, product_id) DO UPDATE SET
                quantity=excluded.quantity
        """;
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cartId);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, quantity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add product to cart", e);
        }
    }

    public void removeProductFromCart(int cartId, int productId) {
        String sql = "DELETE FROM cart_items WHERE cart_id = ? AND product_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cartId);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove product from cart", e);
        }
    }

    public void clearCart(int cartId) {
        String sql = "DELETE FROM cart_items WHERE cart_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cartId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear cart", e);
        }
    }

    // --- ORDER
    public void saveOrder(Order order) {
        String upsertOrder = """
            INSERT INTO orders (customer_id, subtotal, shipping_cost, total_amount, created_at)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT(customer_id, created_at) DO UPDATE SET
                subtotal=excluded.subtotal,
                shipping_cost=excluded.shipping_cost,
                total_amount=excluded.total_amount
        """;
        String selectOrderId = "SELECT id FROM orders WHERE customer_id = ? AND created_at = ?";
        String upsertOrderItem = """
            INSERT INTO order_items (order_id, product_id, quantity, price_at_purchase)
            VALUES (?, ?, ?, ?)
            ON CONFLICT(order_id, product_id) DO UPDATE SET
                quantity=excluded.quantity,
                price_at_purchase=excluded.price_at_purchase
        """;
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            // Update-insert (upsert) order
            try (PreparedStatement upsertStmt = conn.prepareStatement(upsertOrder)) {
                upsertStmt.setString(1, order.getCustomer().getId());
                upsertStmt.setDouble(2, order.getSubtotal());
                upsertStmt.setDouble(3, order.getShippingCost());
                upsertStmt.setDouble(4, order.getTotalAmount());
                upsertStmt.setString(5, order.getCreatedAt().toString());
                upsertStmt.executeUpdate();
            }

            // Get order ID
            int orderId = -1;
            try (PreparedStatement selectStmt = conn.prepareStatement(selectOrderId)) {
                selectStmt.setString(1, order.getCustomer().getId());
                selectStmt.setString(2, order.getCreatedAt().toString());
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    orderId = rs.getInt("id");
                } else {
                    throw new SQLException("Order ID not found after upsert.");
                }
            }

            // Upsert order items
            try (PreparedStatement itemStmt = conn.prepareStatement(upsertOrderItem)) {
                for (Map.Entry<Product, Integer> entry : order.getItems().entrySet()) {
                    Product product = entry.getKey();
                    int quantity = entry.getValue();
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, product.getId());
                    itemStmt.setInt(3, quantity);
                    itemStmt.setDouble(4, product.getPrice());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save order", e);
        }
    }

    public Order getOrder(int orderId) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Customer customer = getCustomer(rs.getString("customer_id"));
                Map<Product, Integer> items = getOrderItems(orderId);
                double subtotal = rs.getDouble("subtotal");
                double shippingCost = rs.getDouble("shipping_cost");
                double totalAmount = rs.getDouble("total_amount");
                LocalDateTime createdAt = LocalDateTime.parse(rs.getString("created_at"));
                return new Order(customer, items, subtotal, shippingCost, totalAmount, createdAt);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch order", e);
        }
    }

    public Map<Product, Integer> getOrderItems(int orderId) {
        String sql = "SELECT product_id, quantity FROM order_items WHERE order_id = ?";
        Map<Product, Integer> items = new HashMap<>();
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Product p = getProduct(rs.getInt("product_id"));
                items.put(p, rs.getInt("quantity"));
            }
            return items;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch order items", e);
        }
    }

    public void deleteOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete order", e);
        }
    }

    public Order getOrderByCustomerAndCreatedAt(String customerId, java.time.LocalDateTime createdAt) {
    String sql = "SELECT id FROM orders WHERE customer_id = ? AND created_at = ?";
    try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, customerId);
        pstmt.setString(2, createdAt.toString());
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return getOrder(rs.getInt("id"));
        }
        return null;
    } catch (SQLException e) {
        throw new RuntimeException("Failed to fetch order by customer and createdAt", e);
    }
    }
}
