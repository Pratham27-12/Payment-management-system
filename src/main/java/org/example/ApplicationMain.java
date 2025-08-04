package org.example;

import org.example.model.UserLifeCycleManagementResponse;
import org.example.model.enums.PaymentCategory;
import org.example.model.enums.PaymentStatus;
import org.example.model.enums.PaymentType;
import org.example.model.enums.UserRole;
import org.example.repository.jdbc.dao.Payment;
import org.example.service.*;
import org.example.service.impl.*;
import org.example.util.PrinterUtil;

import java.util.Scanner;

public class ApplicationMain {
    public static void main(String[] args) {
        UserManagementService userService = new UserManagementServiceImpl();
        Scanner in = new Scanner(System.in);
        String userName = "", password = "";
        UserRole sessionUserRole = null;

        System.out.println("--------------------------------------------------\n     Welcome To Payment Management System     ");
        while (true) {
            System.out.print("Already a user? (Yes/No): ");
            String answer = in.nextLine().trim();
            if (answer.equalsIgnoreCase("YES")) {
                System.out.print("Enter User ID: "); userName = in.nextLine();
                System.out.print("Enter Password: "); password = in.nextLine();
                try {
                    sessionUserRole = userService.verifyUser(userName, password).join();
                    System.out.println("Login Successful as " + sessionUserRole); break;
                } catch (Exception e) { System.out.println("Login Failed: " + e.getMessage()); }
            } else if (answer.equalsIgnoreCase("NO")) {
                System.out.print("Want to register? (Yes/No): ");
                if (in.nextLine().trim().equalsIgnoreCase("YES")) {
                    System.out.print("Enter new UserName: "); userName = in.nextLine();
                    System.out.print("Enter new Password: "); password = in.nextLine();
                    try {
                        UserLifeCycleManagementResponse res = userService.createUser(userName, password).join();
                        if ("SUCCESS".equalsIgnoreCase(res.getStatus())) {
                            System.out.println("User Created Successfully"); break;
                        } else { System.out.println("Failed to create user: " + res.getMessage()); }
                    } catch (Exception e) { System.out.println("Registration Failed: " + e.getMessage()); }
                }
            } else { System.out.println("Invalid input. Please type Yes or No."); }
        }
        clearConsole();
        handleMenu(sessionUserRole, userName, password, in);
    }

    private static void handleMenu(UserRole role, String userName, String password, Scanner in) {
        PaymentManagementService paymentService = new PaymentManagementServiceImpl();
        UserManagementServiceImpl userService = new UserManagementServiceImpl();
        AuditTrailManagementService auditService = new AuditTrailManagementServiceImpl();

        String[][] menus = {
                {"Update User Role", "View All Payments", "View All Users", "Create New Payment", "Generate Monthly Report", "Generate Quarterly Report", "Exit"},
                {"View All Payments", "View All Users", "Update Payment Status", "Generate Monthly Report", "Generate Quarterly Report", "Get Logs of Particular Payments", "Exit"},
                {"View All Payments", "Exit"}
        };
        System.out.println("--------------------------------------------------");
        System.out.println("     Welcome " + role + ": " + userName + "     ");

        while (true) {
            System.out.println("\nSelect an option: ");
            String[] menu = menus[role.ordinal()];
            for (int i = 0; i < menu.length; i++) System.out.println((i + 1) + ". " + menu[i]);
            int choice = in.nextInt(); in.nextLine();

            if (role == UserRole.ADMIN) {
                switch (choice) {
                    case 1: // Update User Role
                        System.out.print("Enter UserName to update: "); String userToUpdate = in.nextLine();
                        System.out.print("Enter new Role (ADMIN, FINANCE_MANAGER, VIEWER): ");
                        userService.updateUserRole(userName, password, userToUpdate, in.nextLine().toUpperCase())
                                .whenComplete((res, t) -> System.out.println(t != null ? "Internal Server Error " + t.getMessage() : res.getMessage()));
                        break;
                    case 2: // View All Payments
                        paymentService.getAllPayment().whenComplete((res, t) -> {
                            if (t != null) System.out.println("Internal Server Error " + t.getMessage());
                            PrinterUtil.printPaymentsTable(res.getPayments());
                        }); break;
                    case 3: // View All Users
                        userService.getAllUsers().whenComplete((res, t) -> {
                            if (t != null) System.out.println("Internal Server Error " + t.getMessage());
                            PrinterUtil.printUsersTable(res);
                        }); break;
                    case 4: // Create New Payment
                        Payment payment = new Payment();
                        System.out.print("Payment ID: "); payment.setId(in.nextLine());
                        System.out.print("Amount: "); payment.setAmount(in.nextLine());
                        System.out.print("Currency: "); payment.setCurrency(in.nextLine());
                        System.out.print("Account Name: "); payment.setAccountName(in.nextLine());
                        System.out.print("Status (PENDING, COMPLETED, FAILED): ");
                        payment.setStatus(PaymentStatus.valueOf(in.nextLine().toUpperCase()));
                        System.out.print("Payment Type (INCOMING, OUTGOING): ");
                        payment.setType(PaymentType.valueOf(in.nextLine().toUpperCase()));
                        System.out.print("Payment Category (SALARY, INVOICE, REFUND, VENDOR_SETTLEMENT): ");
                        payment.setCategory(PaymentCategory.valueOf(in.nextLine().toUpperCase()));
                        payment.setUserName(userName);
                        paymentService.createPaymentRecord(payment, userName, password)
                                .whenComplete((res, t) -> {
                                    if (t != null) System.out.println("Internal Server Error " + t.getMessage());
                                    else System.out.println(res.getMessage());
                                }); break;
                    case 5: // Monthly Report
                        System.out.print("Enter Month (1-12): "); int month = in.nextInt();
                        System.out.print("Enter Year (e.g., 2023): "); int year = in.nextInt(); in.nextLine();
                        paymentService.generateMonthlyReport(month, year)
                                .whenComplete((res, t) -> {
                                    if (t != null) System.out.println("Internal Server Error " + t.getMessage());
                                    else PrinterUtil.printReport(res);
                                }); break;
                    case 6: // Quarterly Report
                        System.out.print("Enter Quarter (1-4): "); int quarter = in.nextInt();
                        System.out.print("Enter Year (e.g., 2023): "); int qYear = in.nextInt(); in.nextLine();
                        paymentService.generateQuarterlyReport(quarter, qYear)
                                .whenComplete((res, t) -> {
                                    if (t != null) System.out.println("Internal Server Error " + t.getMessage());
                                    else PrinterUtil.printReport(res);
                                }); break;
                    case 7: System.out.println("Exiting..."); return;
                    default: System.out.println("Invalid choice. Please try again.");
                }
            } else if (role == UserRole.FINANCE_MANAGER) {
                switch (choice) {
                    case 1: paymentService.getAllPayment().whenComplete((res, t) -> {
                        if (t != null) System.out.println("Internal Server Error " + t.getMessage());
                        PrinterUtil.printPaymentsTable(res.getPayments());
                    }); break;
                    case 2: userService.getAllUsers().whenComplete((res, t) -> {
                        if (t != null) System.out.println("Internal Server Error " + t.getMessage());
                        PrinterUtil.printUsersTable(res);
                    }); break;
                    case 3:
                        System.out.print("Payment Id to update status : "); String paymentId = in.nextLine();
                        System.out.print("Enter new Status (PENDING, COMPLETED, FAILED): ");
                        paymentService.updatePayment(paymentId, userName, password, in.nextLine().toUpperCase())
                                .whenComplete((res, t) -> System.out.println(t != null ? "Internal Server Error " + t.getMessage() : res.getMessage()));
                        break;
                    case 4:
                        System.out.print("Enter Month (1-12): "); int month = in.nextInt();
                        System.out.print("Enter Year (e.g., 2023): "); int year = in.nextInt(); in.nextLine();
                        paymentService.generateMonthlyReport(month, year)
                                .whenComplete((res, t) -> {
                                    if (t != null) System.out.println("Internal Server Error " + t.getMessage());
                                    else PrinterUtil.printReport(res);
                                }); break;
                    case 5:
                        System.out.print("Enter Quarter (1-4): "); int quarter = in.nextInt();
                        System.out.print("Enter Year (e.g., 2023): "); int qYear = in.nextInt(); in.nextLine();
                        paymentService.generateQuarterlyReport(quarter, qYear)
                                .whenComplete((res, t) -> {
                                    if (t != null) System.out.println("Internal Server Error " + t.getMessage());
                                    else PrinterUtil.printReport(res);
                                }); break;
                    case 6:
                        System.out.print("Enter Payment ID to get logs: "); String paymentIdForLogs = in.nextLine();
                        auditService.getAuditTrailById(paymentIdForLogs, userName, password)
                                .whenComplete((res, t) -> {
                                    if (t != null) System.out.println("Internal Server Error " + t.getMessage());
                                    else PrinterUtil.printPaymentsAuditTable(res);
                                }); break;
                    case 7: System.out.println("Exiting..."); return;
                    default: System.out.println("Invalid choice. Please try again.");
                }
            } else if (role == UserRole.VIEWER) {
                switch (choice) {
                    case 1: paymentService.getAllPayment().whenComplete((res, t) -> {
                        if (t != null) System.out.println("Internal Server Error " + t.getMessage());
                        PrinterUtil.printPaymentsTable(res.getPayments());
                    }); break;
                    case 2: System.out.println("Exiting..."); return;
                    default: System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    public static void clearConsole() {
        for (int i = 0; i < 45; i++) System.out.println();
    }
}