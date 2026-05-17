package by.bsuir.electroshop.client;

import by.bsuir.electroshop.common.dto.*;
import by.bsuir.electroshop.common.enums.CommandType;
import by.bsuir.electroshop.common.enums.Role;
import by.bsuir.electroshop.common.model.InventoryItem;
import by.bsuir.electroshop.common.model.SaleTransaction;
import by.bsuir.electroshop.common.model.TransactionItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ElectroShopClientApp extends JFrame {
    private final SocketClient client;
    private LoginResponse currentUser;

    private final JTextField usernameField = new JTextField("admin");
    private final JPasswordField passwordField = new JPasswordField("admin123");
    private final JTextField searchField = new JTextField(22);
    private final JLabel statusLabel = new JLabel("Не авторизован");
    private final JTextArea outputArea = new JTextArea();
    private final InventoryTableModel inventoryTableModel = new InventoryTableModel();
    private final JTable inventoryTable = new JTable(inventoryTableModel);

    public ElectroShopClientApp() throws IOException {
        super("ElectroShop Client");
        this.client = new SocketClient("localhost", 5555);
        configureFrame();
        configureTable();
        setJMenuBar(createMenuBar());
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
    }

    private void configureFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1220, 760);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));
        getRootPane().setBorder(new EmptyBorder(12, 12, 12, 12));
    }

    private void configureTable() {
        inventoryTable.setRowHeight(26);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.setAutoCreateRowSorter(true);
        inventoryTable.setFillsViewportHeight(true);

        JTableHeader header = inventoryTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
        header.setFont(header.getFont().deriveFont(Font.BOLD, 13f));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        inventoryTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        inventoryTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        inventoryTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        inventoryTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu catalogMenu = new JMenu("Каталог");
        JMenuItem refreshItem = new JMenuItem("Обновить товары");
        refreshItem.addActionListener(e -> loadProducts());
        JMenuItem searchItem = new JMenuItem("Поиск");
        searchItem.addActionListener(e -> performSearch());
        catalogMenu.add(refreshItem);
        catalogMenu.add(searchItem);

        JMenu salesMenu = new JMenu("Продажи");
        JMenuItem createSaleItem = new JMenuItem("Оформить продажу");
        createSaleItem.addActionListener(e -> createDemoSale());
        JMenuItem mySalesItem = new JMenuItem("Мои продажи");
        mySalesItem.addActionListener(e -> loadMySales());
        JMenuItem allSalesItem = new JMenuItem("Все продажи");
        allSalesItem.addActionListener(e -> loadAllSales());
        salesMenu.add(createSaleItem);
        salesMenu.add(mySalesItem);
        salesMenu.add(allSalesItem);

        JMenu adminMenu = new JMenu("Администрирование");
        JMenuItem statsItem = new JMenuItem("Статистика");
        statsItem.addActionListener(e -> loadStats());
        JMenuItem categoryItem = new JMenuItem("Создать категорию");
        categoryItem.addActionListener(e -> createCategory());
        JMenuItem productItem = new JMenuItem("Создать товар");
        productItem.addActionListener(e -> createProduct());
        JMenuItem userItem = new JMenuItem("Создать пользователя");
        userItem.addActionListener(e -> createUser());
        JMenuItem stockItem = new JMenuItem("Пополнить склад");
        stockItem.addActionListener(e -> updateStock());
        adminMenu.add(statsItem);
        adminMenu.addSeparator();
        adminMenu.add(categoryItem);
        adminMenu.add(productItem);
        adminMenu.add(userItem);
        adminMenu.add(stockItem);

        menuBar.add(catalogMenu);
        menuBar.add(salesMenu);
        menuBar.add(adminMenu);
        return menuBar;
    }

    private JPanel createHeaderPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(12, 12));
        wrapper.add(createTitlePanel(), BorderLayout.NORTH);
        wrapper.add(createControlsPanel(), BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(4, 4, 4, 4));

        JLabel title = new JLabel("Система учета товаров магазина электротоваров");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JLabel subtitle = new JLabel("Клиентское приложение: поиск, продажи, склад, статистика");
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 13f));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitle);

        panel.add(textPanel, BorderLayout.WEST);
        panel.add(createUserCard(), BorderLayout.EAST);
        return panel;
    }

    private JPanel createUserCard() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 6));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(8, 12, 8, 12)
        ));
        panel.add(new JLabel("Статус сеанса"));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        panel.add(statusLabel);
        return panel;
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.gridy = 0;

        usernameField.setColumns(10);
        passwordField.setColumns(10);

        gbc.gridx = 0;
        panel.add(new JLabel("Логин"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Пароль"), gbc);
        gbc.gridx = 3;
        panel.add(passwordField, gbc);
        gbc.gridx = 4;
        panel.add(createPrimaryButton("Войти", this::doLogin), gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Поиск по бренду, модели, характеристикам"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
        panel.add(searchField, gbc);
        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(createPrimaryButton("Поиск", this::performSearch), gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(createActionButton("Обновить товары", this::loadProducts), gbc);
        gbc.gridx = 1;
        panel.add(createActionButton("Оформить продажу", this::createDemoSale), gbc);
        gbc.gridx = 2;
        panel.add(createActionButton("Мои продажи", this::loadMySales), gbc);
        gbc.gridx = 3;
        panel.add(createActionButton("Все продажи", this::loadAllSales), gbc);
        gbc.gridx = 4;
        panel.add(createActionButton("Статистика", this::loadStats), gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(createActionButton("Создать категорию", this::createCategory), gbc);
        gbc.gridx = 1;
        panel.add(createActionButton("Создать товар", this::createProduct), gbc);
        gbc.gridx = 2;
        panel.add(createActionButton("Создать пользователя", this::createUser), gbc);
        gbc.gridx = 3;
        panel.add(createActionButton("Пополнить склад", this::updateStock), gbc);
        gbc.gridx = 4;
        panel.add(createActionButton("Редактировать товар", this::editProduct), gbc);
        gbc.gridy = 4;
        gbc.gridx = 3;
        panel.add(createActionButton("Удалить товар", this::deleteProduct), gbc);
        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(createActionButton("История продаж", this::loadAllSales), gbc);
        gbc.gridx = 1;
        panel.add(createActionButton("Показать статистику", this::loadStats), gbc);
        gbc.gridx = 2;
        panel.add(createActionButton("Очистить журнал", () -> outputArea.setText("")), gbc);

        return panel;
    }

    private JButton createPrimaryButton(String title, Runnable action) {
        JButton button = new JButton(title);
        button.setFocusPainted(false);
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        button.addActionListener(e -> action.run());
        return button;
    }

    private JButton createActionButton(String title, Runnable action) {
        JButton button = new JButton(title);
        button.setFocusPainted(false);
        button.addActionListener(e -> action.run());
        return button;
    }

    private JComponent createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.add(createTableCard(), BorderLayout.CENTER);
        panel.add(createRightPanel(), BorderLayout.EAST);
        return panel;
    }

    private JPanel createTableCard() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel label = new JLabel("Каталог товаров");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setPreferredSize(new Dimension(350, 100));
        panel.add(createHintCard(), BorderLayout.NORTH);
        panel.add(createLogCard(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHintCard() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel label = new JLabel("Подсказки");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 15f));

        JTextArea hints = new JTextArea();
        hints.setEditable(false);
        hints.setLineWrap(true);
        hints.setWrapStyleWord(true);
        hints.setText(
                "Тестовые аккаунты:\n" +
                        "admin / admin123\n" +
                        "manager / manager123\n" +
                        "seller / seller123\n\n" +
                        "Как работать:\n" +
                        "1. Выполни вход.\n" +
                        "2. Нажми 'Обновить товары'.\n" +
                        "3. Для поиска введи бренд, модель или тип техники.\n" +
                        "4. Для продажи выбери строку в таблице."
        );
        hints.setBackground(panel.getBackground());

        panel.add(label, BorderLayout.NORTH);
        panel.add(hints, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLogCard() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel label = new JLabel("Журнал операций");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 15f));

        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setMargin(new Insets(8, 8, 8, 8));

        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(4, 2, 2, 2));
        panel.add(new JLabel("Готово к подключению к серверу localhost:5555"), BorderLayout.WEST);
        return panel;
    }

    private void doLogin() {
        try {
            Response response = client.send(Request.of(CommandType.LOGIN,
                    new LoginRequest(usernameField.getText().trim(), new String(passwordField.getPassword()))));
            appendResponse(response);
            if (response.isSuccess()) {
                currentUser = (LoginResponse) response.getData();
                statusLabel.setText(currentUser.username() + " (" + currentUser.role() + ")");
                appendLine("Текущий пользователь: " + currentUser.username() + " / " + currentUser.role());
                applyRolePermissions();
            }
        } catch (Exception e) {
            appendLine("Ошибка входа: " + e.getMessage());
        }
    }


    private void applyRolePermissions() {
        Role role = currentUser.role();
        appendLine("Права доступа обновлены для роли: " + role);

        if (role == Role.SELLER) {
            appendLine("Доступ продавца: продажи, поиск и просмотр каталога");
        } else if (role == Role.MANAGER) {
            appendLine("Доступ менеджера: управление товарами и складом");
        } else if (role == Role.ADMIN) {
            appendLine("Доступ администратора: полный доступ к системе");
        }
    }

    private void editProduct() {
        if (!checkLogin()) return;

        int row = inventoryTable.getSelectedRow();
        if (row < 0) {
            appendLine("Выберите товар для редактирования");
            return;
        }

        InventoryItem item = inventoryTableModel.getItem(inventoryTable.convertRowIndexToModel(row));

        String newPrice = JOptionPane.showInputDialog(this, "Новая цена", item.getRetailPrice());
        if (newPrice == null || newPrice.isBlank()) return;

        try {
            double price = Double.parseDouble(newPrice);
            sendAndMaybeUpdateTable(Request.of(CommandType.UPDATE_PRODUCT_PRICE, sessionUser(), new UpdatePriceRequest(item.getId(), price)), false);
            appendLine("Цена товара обновлена");
            loadProducts();
        } catch (Exception e) {
            appendLine("Ошибка изменения товара: " + e.getMessage());
        }
    }

    private void loadProducts() {
        sendAndMaybeUpdateTable(Request.of(CommandType.LIST_PRODUCTS, sessionUser(), null), true);
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isBlank()) {
            appendLine("Введите текст для поиска");
            return;
        }
        sendAndMaybeUpdateTable(Request.of(CommandType.SEARCH_PRODUCTS, sessionUser(), keyword), true);
    }

    private void createDemoSale() {
        if (!checkLogin()) return;
        int row = inventoryTable.getSelectedRow();
        if (row < 0) {
            appendLine("Сначала выбери товар в таблице");
            return;
        }
        InventoryItem item = inventoryTableModel.getItem(inventoryTable.convertRowIndexToModel(row));
        String qtyText = JOptionPane.showInputDialog(this, "Количество", "1");
        if (qtyText == null) return;
        int qty = Integer.parseInt(qtyText);
        List<TransactionItem> items = List.of(new TransactionItem(item.getId(), item.getBrand() + " " + item.getModel(), qty, item.getRetailPrice()));
        SaleRequest saleRequest = new SaleRequest(currentUser.username(), "Покупатель", items);
        sendAndMaybeUpdateTable(Request.of(CommandType.CREATE_SALE, sessionUser(), saleRequest), false);
        loadProducts();
    }

    private void loadMySales() {
        sendAndMaybeUpdateTable(Request.of(CommandType.GET_MY_SALES, sessionUser(), null), false);
    }

    private void loadAllSales() {
        sendAndMaybeUpdateTable(Request.of(CommandType.GET_ALL_SALES, sessionUser(), null), false);
    }

    private void loadStats() {
        sendAndMaybeUpdateTable(Request.of(CommandType.DASHBOARD_STATS, sessionUser(), null), false);
        sendAndMaybeUpdateTable(Request.of(CommandType.SALES_BY_EMPLOYEE, sessionUser(), null), false);
        sendAndMaybeUpdateTable(Request.of(CommandType.SALES_BY_CATEGORY, sessionUser(), null), false);
    }

    private void createCategory() {
        if (!checkLogin()) return;
        String name = JOptionPane.showInputDialog(this, "Название категории", "Кабели");
        if (name == null || name.isBlank()) return;
        sendAndMaybeUpdateTable(Request.of(CommandType.CREATE_CATEGORY, sessionUser(), new CategoryCreateRequest(name, "Создано из клиента")), false);
    }

    private void createProduct() {
        if (!checkLogin()) return;

        // Получаем список категорий для выпадающего списка
        JTextField brandField = new JTextField(15);
        JTextField modelField = new JTextField(15);
        JTextField specField  = new JTextField(15);
        JTextField priceField = new JTextField("0.00", 15);
        JTextField stockField = new JTextField("0", 15);
        JTextField warrantyField = new JTextField("12", 15);
        JTextField categoryIdField = new JTextField("1", 5);

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("ID категории:"));   form.add(categoryIdField);
        form.add(new JLabel("Бренд:"));          form.add(brandField);
        form.add(new JLabel("Модель:"));         form.add(modelField);
        form.add(new JLabel("Характеристики:")); form.add(specField);
        form.add(new JLabel("Цена:"));           form.add(priceField);
        form.add(new JLabel("Остаток:"));        form.add(stockField);
        form.add(new JLabel("Гарантия (мес.):")); form.add(warrantyField);

        int result = JOptionPane.showConfirmDialog(this, form,
                "Добавить товар", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        // Валидация
        if (brandField.getText().isBlank() || modelField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Бренд и модель обязательны!", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int categoryId  = Integer.parseInt(categoryIdField.getText().trim());
            double price    = Double.parseDouble(priceField.getText().trim());
            int stock       = Integer.parseInt(stockField.getText().trim());
            int warranty    = Integer.parseInt(warrantyField.getText().trim());

            if (price < 0 || stock < 0 || warranty < 0) {
                JOptionPane.showMessageDialog(this, "Цена, остаток и гарантия не могут быть отрицательными!", "Ошибка", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ProductCreateRequest request = new ProductCreateRequest(
                    categoryId,
                    brandField.getText().trim(),
                    modelField.getText().trim(),
                    specField.getText().trim(),
                    price, stock, warranty
            );
            sendAndMaybeUpdateTable(Request.of(CommandType.CREATE_PRODUCT, sessionUser(), request), false);
            loadProducts();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Некорректные числовые данные!", "Ошибка", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteProduct() {
        if (!checkLogin()) return;

        int row = inventoryTable.getSelectedRow();
        if (row < 0) {
            appendLine("Выберите товар для удаления");
            return;
        }

        InventoryItem item = inventoryTableModel.getItem(inventoryTable.convertRowIndexToModel(row));

        int confirm = JOptionPane.showConfirmDialog(this,
                "Удалить товар: " + item.getBrand() + " " + item.getModel() + "?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            sendAndMaybeUpdateTable(
                    Request.of(CommandType.DELETE_PRODUCT, sessionUser(), item.getId()),
                    false
            );
            loadProducts();
        } catch (Exception e) {
            appendLine("Ошибка удаления: " + e.getMessage());
        }
    }

    private void createUser() {
        if (!checkLogin()) return;
        UserCreateRequest request = new UserCreateRequest("cashier1", "cash123", "Олег", "Кассир", "+375290000000", Role.SELLER);
        sendAndMaybeUpdateTable(Request.of(CommandType.CREATE_USER, sessionUser(), request), false);
    }

    private void updateStock() {
        if (!checkLogin()) return;
        int row = inventoryTable.getSelectedRow();
        if (row < 0) {
            appendLine("Сначала выбери товар");
            return;
        }
        InventoryItem item = inventoryTableModel.getItem(inventoryTable.convertRowIndexToModel(row));
        sendAndMaybeUpdateTable(Request.of(CommandType.UPDATE_STOCK, sessionUser(), new UpdateStockRequest(item.getId(), 3)), false);
        loadProducts();
    }

    private void sendAndMaybeUpdateTable(Request request, boolean updateTable) {
        if (request.getCommandType() != CommandType.LOGIN && !checkLogin()) return;
        try {
            Response response = client.send(request);
            appendResponse(response);
            if (response.isSuccess() && updateTable && response.getData() instanceof List<?> list) {
                List<InventoryItem> items = new ArrayList<>();
                for (Object o : list) {
                    items.add((InventoryItem) o);
                }
                inventoryTableModel.setItems(items);
            }
        } catch (Exception e) {
            appendLine("Ошибка запроса: " + e.getMessage());
        }
    }

    private String sessionUser() {
        return currentUser == null ? null : currentUser.username();
    }

    private boolean checkLogin() {
        if (currentUser == null) {
            appendLine("Сначала войди в систему");
            return false;
        }
        return true;
    }

    private void appendResponse(Response response) {
        appendLine((response.isSuccess() ? "OK: " : "ERROR: ") + response.getMessage());
        if (response.getData() == null) {
            appendLine("------------------------------");
            return;
        }
        Object data = response.getData();
        if (data instanceof StatsSummary summary) {
            appendLine("Товаров: " + summary.productsCount() + ", категорий: " + summary.categoriesCount() +
                    ", продаж: " + summary.salesCount() + ", выручка: " + summary.revenue() +
                    ", позиций с низким остатком: " + summary.lowStockCount());
        } else if (data instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof SaleTransaction sale) {
                    appendLine("Продажа #" + sale.getId() + " | " + sale.getEmployeeUsername() + " | " + sale.getTotalAmount());
                    for (TransactionItem transactionItem : sale.getItems()) {
                        appendLine("   - " + transactionItem.getProductName() + " x" + transactionItem.getQuantity());
                    }
                } else {
                    appendLine(String.valueOf(item));
                }
            }
        } else {
            appendLine(String.valueOf(data));
        }
        appendLine("------------------------------");
    }

    private void appendLine(String text) {
        outputArea.append(text + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ElectroShopClientApp().setVisible(true);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Не удалось подключиться к серверу: " + e.getMessage());
            }
        });
    }

    private static class InventoryTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Категория", "Бренд", "Модель", "Цена", "Остаток", "Гарантия"};
        private List<InventoryItem> items = new ArrayList<>();

        public void setItems(List<InventoryItem> items) {
            this.items = items;
            fireTableDataChanged();
        }

        public InventoryItem getItem(int row) {
            return items.get(row);
        }

        @Override
        public int getRowCount() {
            return items.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            InventoryItem item = items.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> item.getId();
                case 1 -> item.getCategoryName();
                case 2 -> item.getBrand();
                case 3 -> item.getModel();
                case 4 -> item.getRetailPrice();
                case 5 -> item.getStockBalance();
                case 6 -> item.getWarrantyMonths();
                default -> "";
            };
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }
    }
}
