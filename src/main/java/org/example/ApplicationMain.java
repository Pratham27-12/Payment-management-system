package org.example;

import org.example.model.UserLifeCycleManagementResponse;
import org.example.model.enums.PaymentCategory;
import org.example.model.enums.PaymentStatus;
import org.example.model.enums.PaymentType;
import org.example.model.enums.UserRole;
import org.example.repository.jdbc.dao.Payment;
import org.example.service.AuditTrailManagementService;
import org.example.service.PaymentManagementService;
import org.example.service.UserManagementService;
import org.example.service.impl.AuditTrailManagementServiceImpl;
import org.example.service.impl.PaymentManagementServiceImpl;
import org.example.service.impl.UserManagementServiceImpl;
import org.example.util.PrinterUtil;

import java.util.Scanner;

public class ApplicationMain {
    public static void main(String[] args) {
        UserManagementService userService = new UserManagementServiceImpl();
        Scanner in = new Scanner(System.in);
        UserRole sessionUserRole = null;
        String userName, password;
        System.out.println("--------------------------------------------------");
        System.out.println("     Welcome To Payment Management System     ");

        while (true) {
            System.out.print("Already a user? (Yes/No): ");
            String answer = in.nextLine().trim();

            if (answer.equalsIgnoreCase("YES")) {
                System.out.print("Enter User ID: ");
                userName = in.nextLine();
                System.out.print("Enter Password: ");
                password = in.nextLine();

                try {
                    sessionUserRole = userService.verifyUser(userName, password).join();
                    System.out.println("Login Successful as " + sessionUserRole);
                    break;
                } catch (Exception e) {
                    System.out.println("Login Failed: " + e.getMessage());
                }

            } else if (answer.equalsIgnoreCase("NO")) {
                System.out.print("Want to register? (Yes/No): ");
                String register = in.nextLine().trim();

                if (register.equalsIgnoreCase("YES")) {
                    System.out.print("Enter new UserName: ");
                    userName = in.nextLine();
                    System.out.print("Enter new Password: ");
                    password = in.nextLine();

                    try {
                        UserLifeCycleManagementResponse res = userService.createUser(userName, password).join();
                        if ("SUCCESS".equalsIgnoreCase(res.getStatus())) {
                            System.out.println("User Created Successfully");
                            break;
                        } else {
                            System.out.println("Failed to create user: " + res.getMessage());
                        }
                    } catch (Exception e) {
                        System.out.println("Registration Failed: " + e.getMessage());
                    }
                }

            } else {
                System.out.println("Invalid input. Please type Yes or No.");
            }
        }
        clearConsole();
        switch (sessionUserRole) {
            case ADMIN -> ifUserIsAdmin(userName, password);
            case FINANCE_MANAGER -> ifUserIsFinanceManager(userName, password);
            case VIEWER -> ifUserIsViewer(userName, password);
        }
    }

    private static void ifUserIsAdmin(String userName, String password) {
        PaymentManagementService paymentManagementService = new PaymentManagementServiceImpl();
        UserManagementServiceImpl userManagementService = new UserManagementServiceImpl();
        Scanner in = new Scanner(System.in);

        System.out.println("--------------------------------------------------");
        System.out.println("     Welcome Admin: " + userName + "     ");
        System.out.println();
        while (true) {
            System.out.println();
            System.out.println("Select an option: ");
            System.out.println();
            System.out.println("1. Update User Role");
            System.out.println("2. View All Payments");
            System.out.println("3. View All Users");
            System.out.println("4. Create New Payment");
            System.out.println("5. Generate Monthly Report");
            System.out.println("6. Generate Quarterly Report");
            System.out.println("7. Exit");

            int choice = in.nextInt();
            in.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter UserName to update: ");
                    String userToUpdate = in.nextLine();
                    System.out.print("Enter new Role (ADMIN, FINANCE_MANAGER, VIEWER): ");
                    String role = in.nextLine().toUpperCase();
                    userManagementService.updateUserRole(userName, password, userToUpdate, role).whenComplete((res, throwable) -> {
                        if (throwable != null) {
                            System.out.println("Internal Server Error " + throwable.getMessage());
                        } else {
                            System.out.println(res.getMessage());
                        }
                    });
                    break;
                case 2:
                    paymentManagementService.getAllPayment().whenComplete((res, throwable) -> {
                        if (throwable != null) {
                            System.out.println("Internal Server Error " + throwable.getMessage());
                        }
                        PrinterUtil.printPaymentsTable(res.getPayments());
                    });
                    break;
                case 3:
                    userManagementService.getAllUsers().whenComplete((res, throwable) -> {
                        if (throwable != null) {
                            System.out.println("Internal Server Error " + throwable.getMessage());
                        }
                        PrinterUtil.printUsersTable(res);
                    });
                    break;
                case 4:
                    Payment payment = new Payment();
                    System.out.println("Enter Payment Details:");
                    System.out.print("Payment ID: ");
                    payment.setId(in.nextLine());
                    System.out.print("Amount: ");
                    payment.setAmount(in.nextLine());
                    System.out.print("Currency: ");
                    payment.setCurrency(in.nextLine());
                    System.out.print("Status (PENDING, COMPLETED, FAILED): ");
                    payment.setStatus(PaymentStatus.valueOf(in.nextLine().toUpperCase()));
                    System.out.println("Enter Payment Type (INCOMING, OUTGOING): ");
                    payment.setType(PaymentType.valueOf(in.nextLine().toUpperCase()));
                    System.out.println("Enter Payment Category (SALARY, INVOICE, REFUND, VENDOR_SETTLEMENT): ");
                    payment.setCategory(PaymentCategory.valueOf(in.nextLine().toUpperCase()));
                    paymentManagementService.createPaymentRecord(payment, userName, password).whenComplete((res, throwable) -> {
                        if (throwable != null) {
                            System.out.println("Internal Server Error " + throwable.getMessage());
                        } else {
                            PrinterUtil.printPaymentsTable(res.getPayments());
                        }
                    });
                    break;
                case 5:
                    System.out.println("Enter Month (1-12): ");
                    int month = in.nextInt();
                    System.out.println("Enter Year (e.g., 2023): ");
                    int monthlyYear = in.nextInt();
                    paymentManagementService.generateMonthlyReport(month, monthlyYear).whenComplete((res, throwable) -> {
                        if (throwable != null) {
                            System.out.println("Internal Server Error " + throwable.getMessage());
                        } else {
                            PrinterUtil.printReport(res);
                        }
                    });
                    break;
                case 6:
                    System.out.println("Enter Quarter (1-4): ");
                    int quarter = in.nextInt();
                    System.out.println("Enter Year (e.g., 2023): ");
                    int quarterYear = in.nextInt();
                    paymentManagementService.generateQuarterlyReport(quarter, quarterYear).whenComplete((res, throwable) -> {
                        if (throwable != null) {
                            System.out.println("Internal Server Error " + throwable.getMessage());
                        } else {
                            PrinterUtil.printReport(res);
                        }
                    });
                    break;
                case 7:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void ifUserIsFinanceManager(String userName, String password) {
        PaymentManagementService paymentManagementService = new PaymentManagementServiceImpl();
        UserManagementServiceImpl userManagementService = new UserManagementServiceImpl();
        AuditTrailManagementService auditTrailManagementService = new AuditTrailManagementServiceImpl();
        Scanner in = new Scanner(System.in);

        System.out.println("--------------------------------------------------");
        System.out.println("     Welcome Finance Manager : " + userName + "     ");
        System.out.println();

        while (true) {
            System.out.println();
            System.out.println("Select an option: ");
            System.out.println();
            System.out.println("1. View All Payments");
            System.out.println("2. View All Users");
            System.out.println("3. Update Payment Status");
            System.out.println("4. Generate Monthly Report");
            System.out.println("5. Generate Quarterly Report");
            System.out.println("6. Get Logs of Particular Payments");
            System.out.println("7. Exit");

            int choice = in.nextInt();
            in.nextLine();

            switch (choice) {
                case 1:
                    paymentManagementService.getAllPayment().whenComplete((res, throwable) -> {
                        if (throwable != null) {
                            System.out.println("Internal Server Error " + throwable.getMessage());
                        }
                        PrinterUtil.printPaymentsTable(res.getPayments());
                    });
                    break;
                case 2:
                    userManagementService.getAllUsers().whenComplete((res, throwable) -> {
                        if (throwable != null) {
                            System.out.println("Internal Server Error " + throwable.getMessage());
                        }
                        PrinterUtil.printUsersTable(res);
                    });
                    break;
                case 3:
                    System.out.println("Payment Id to update status : ");
                    String paymentId = in.nextLine();
                    System.out.println("Enter new Status (PENDING, COMPLETED, FAILED): ");
                    String status = in.nextLine().toUpperCase();
                    paymentManagementService.updatePayment(paymentId, userName, password, status).whenComplete((res, throwable) -> {
                        if (throwable != null) {
                            System.out.println("Internal Server Error " + throwable.getMessage());
                        } else {
                            System.out.println(res.getMessage());
                        }
                    });
                    break;

                case 4:
                    System.out.println("Enter Month (1-12): ");
                    int month = in.nextInt();
                    System.out.println("Enter Year (e.g., 2023): ");
                    int monthlyYear = in.nextInt();
                    paymentManagementService.generateMonthlyReport(month, monthlyYear).whenComplete((res, throwable) -> {
                        if (throwable != null) {
                            System.out.println("Internal Server Error " + throwable.getMessage());
                        } else {
                            PrinterUtil.printReport(res);
                        }
                    });
                    break;
                case 5:
                    System.out.println("Enter Quarter (1-4): ");
                    int quarter = in.nextInt();
                    System.out.println("Enter Year (e.g., 2023): ");
                    int quarterYear = in.nextInt();
                    paymentManagementService.generateQuarterlyReport(quarter, quarterYear).whenComplete((res, throwable) -> {
                        if (throwable != null) {
                            System.out.println("Internal Server Error " + throwable.getMessage());
                        } else {
                            PrinterUtil.printReport(res);
                        }
                    });
                    break;
                case 6:
                    System.out.println("Enter Payment ID to get logs: ");
                    String paymentIdForLogs = in.nextLine();
                    auditTrailManagementService.getAuditTrailById(paymentIdForLogs, userName, password)
                            .whenComplete((res, throwable) -> {
                                if(throwable != null) {
                                    System.out.println("Internal Server Error " + throwable.getMessage());
                                } else {
                                    PrinterUtil.printPaymentsAuditTable(res);
                                }
                            });
                    break;
                case 7:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        }
    }

    private static void ifUserIsViewer(String userName, String password) {
        PaymentManagementService paymentManagementService = new PaymentManagementServiceImpl();
        UserManagementServiceImpl userManagementService = new UserManagementServiceImpl();
        Scanner in = new Scanner(System.in);

        System.out.println("--------------------------------------------------");
        System.out.println("     Welcome Viewer: " + userName + "     ");
        System.out.println();

        while (true) {
            System.out.println();
            System.out.println("Select an option: ");
            System.out.println();
            System.out.println("1. View All Payments");
            System.out.println("2. Exit");

            int choice = in.nextInt();
            in.nextLine();

            switch (choice) {
                case 1:
                    paymentManagementService.getAllPayment().whenComplete((res, throwable) -> {
                        if (throwable != null) {
                            System.out.println("Internal Server Error " + throwable.getMessage());
                        }
                        PrinterUtil.printPaymentsTable(res.getPayments());
                    });
                    break;
                case 2:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        }
    }

    public static void clearConsole() {
        for (int i = 0; i < 45; i++) {
            System.out.println();
        }
    }
}