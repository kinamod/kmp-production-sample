import SwiftUI

enum InsureCoPalette {
    static let red = Color(red: 0xDA / 255, green: 0x1E / 255, blue: 0x28 / 255)
    static let redDark = Color(red: 0xA2 / 255, green: 0x19 / 255, blue: 0x1F / 255)
    static let layer = Color(red: 0x26 / 255, green: 0x26 / 255, blue: 0x26 / 255)
    static let field = Color(red: 0x39 / 255, green: 0x39 / 255, blue: 0x39 / 255)
    static let fieldBorder = Color(red: 0x8D / 255, green: 0x8D / 255, blue: 0x8D / 255)
    static let textPrimary = Color(red: 0xF4 / 255, green: 0xF4 / 255, blue: 0xF4 / 255)
    static let textSecondary = Color(red: 0xC6 / 255, green: 0xC6 / 255, blue: 0xC6 / 255)
    static let textPlaceholder = Color(red: 0x6F / 255, green: 0x6F / 255, blue: 0x6F / 255)
    static let interactive = Color(red: 0x78 / 255, green: 0xA9 / 255, blue: 0xFF / 255)
    static let secondaryButton = Color(red: 0x6F / 255, green: 0x6F / 255, blue: 0x6F / 255)
    static let warningBackground = Color(red: 0xFF / 255, green: 0xF4 / 255, blue: 0xB4 / 255)
    static let warningText = Color(red: 0x94 / 255, green: 0x6C / 255, blue: 0x00 / 255)
    static let error = Color(red: 0xFF / 255, green: 0x83 / 255, blue: 0x89 / 255)
}

struct SignUpHeader: View {
    var body: some View {
        VStack(spacing: 8) {
            Text("Sign Up for InsureCo")
                .font(.system(size: 28))
            Text("Get started with your insurance coverage in just a few steps")
                .font(.system(size: 16))
        }
        .foregroundStyle(.white)
        .multilineTextAlignment(.center)
        .frame(maxWidth: .infinity)
        .padding(.horizontal, 24)
        .padding(.vertical, 32)
        .background(
            LinearGradient(
                colors: [InsureCoPalette.red, InsureCoPalette.redDark],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        )
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}

struct StepIndicator: View {
    let steps: [String]
    let currentStep: Int

    var body: some View {
        HStack(alignment: .top, spacing: 8) {
            ForEach(Array(steps.enumerated()), id: \.offset) { index, label in
                let isComplete = index < currentStep
                let isCurrent = index == currentStep
                VStack(alignment: .leading, spacing: 8) {
                    Rectangle()
                        .fill(isComplete || isCurrent ? InsureCoPalette.interactive : InsureCoPalette.fieldBorder)
                        .frame(height: 2)
                    HStack(alignment: .top, spacing: 6) {
                        Image(systemName: isComplete ? "checkmark.circle.fill" : isCurrent ? "circle.lefthalf.filled" : "circle.dashed")
                            .font(.system(size: 12))
                            .foregroundStyle(isComplete || isCurrent ? InsureCoPalette.interactive : InsureCoPalette.textSecondary)
                            .padding(.top, 2)
                        VStack(alignment: .leading, spacing: 0) {
                            Text("Step \(index + 1)")
                                .font(.system(size: 14))
                                .foregroundStyle(InsureCoPalette.textPrimary)
                            Text(label)
                                .font(.system(size: 12))
                                .foregroundStyle(InsureCoPalette.textSecondary)
                        }
                        .lineLimit(1)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .accessibilityElement(children: .combine)
            }
        }
        .padding(16)
        .background(InsureCoPalette.layer)
        .clipShape(RoundedRectangle(cornerRadius: 4))
    }
}

struct WarningBanner: View {
    let message: String
    let onDismiss: () -> Void

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: "exclamationmark.circle")
                .font(.system(size: 14))
            Text(message)
                .font(.system(size: 14))
                .frame(maxWidth: .infinity, alignment: .leading)
            Button("Dismiss", action: onDismiss)
                .font(.system(size: 14, weight: .semibold))
        }
        .foregroundStyle(InsureCoPalette.warningText)
        .tint(InsureCoPalette.warningText)
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(InsureCoPalette.warningBackground)
        .overlay(Rectangle().stroke(Color.black, lineWidth: 1))
    }
}

struct FormField<Content: View>: View {
    let label: String
    var isRequired: Bool = true
    var helperText: String? = nil
    var errorText: String? = nil
    @ViewBuilder let content: Content

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(label)
                .font(.system(size: 12, weight: isRequired ? .semibold : .regular))
                .foregroundStyle(InsureCoPalette.textSecondary)
            content
            if let text = errorText ?? helperText {
                Text(text)
                    .font(.system(size: 12))
                    .foregroundStyle(errorText != nil ? InsureCoPalette.error : InsureCoPalette.textSecondary)
            }
        }
    }
}

struct FieldBackground: ViewModifier {
    var isError: Bool = false

    func body(content: Content) -> some View {
        content
            .padding(.horizontal, 16)
            .frame(minHeight: 48)
            .background(InsureCoPalette.field)
            .overlay(alignment: .bottom) {
                Rectangle()
                    .fill(isError ? InsureCoPalette.error : InsureCoPalette.fieldBorder)
                    .frame(height: isError ? 2 : 1)
            }
    }
}

extension View {
    func insureCoField(isError: Bool = false) -> some View {
        modifier(FieldBackground(isError: isError))
    }
}

struct InsureCoTextField: View {
    let placeholder: String
    @Binding var text: String
    var isError: Bool = false

    var body: some View {
        TextField(
            "",
            text: $text,
            prompt: Text(placeholder).foregroundColor(InsureCoPalette.textPlaceholder)
        )
        .foregroundStyle(InsureCoPalette.textPrimary)
        .insureCoField(isError: isError)
    }
}

struct YearSelect: View {
    @Binding var year: Int?
    let years: [Int]
    var isError: Bool = false

    var body: some View {
        Menu {
            Picker("Year", selection: $year) {
                ForEach(years, id: \.self) { option in
                    Text(String(option)).tag(Optional(option))
                }
            }
        } label: {
            HStack {
                Text(year.map { String($0) } ?? "Select a year")
                    .foregroundStyle(year == nil ? InsureCoPalette.textPlaceholder : InsureCoPalette.textPrimary)
                Spacer()
                Image(systemName: "chevron.down")
                    .font(.system(size: 14))
                    .foregroundStyle(InsureCoPalette.textPrimary)
            }
            .contentShape(Rectangle())
            .insureCoField(isError: isError)
        }
        .accessibilityLabel("Year")
    }
}

struct NumberStepperField: View {
    let label: String
    @Binding var value: Int
    var step: Int = 1000

    var body: some View {
        HStack(spacing: 0) {
            TextField(label, value: $value, format: .number.grouping(.never))
                .keyboardType(.numberPad)
                .foregroundStyle(InsureCoPalette.textPrimary)
            Button {
                value = max(0, value - step)
            } label: {
                Image(systemName: "minus")
                    .frame(width: 44, height: 44)
            }
            .disabled(value <= 0)
            .accessibilityLabel("Decrease \(label)")
            Divider()
                .frame(height: 16)
                .overlay(InsureCoPalette.fieldBorder)
            Button {
                value += step
            } label: {
                Image(systemName: "plus")
                    .frame(width: 44, height: 44)
            }
            .accessibilityLabel("Increase \(label)")
        }
        .tint(InsureCoPalette.textPrimary)
        .insureCoField()
    }
}

struct InsureCoButtonStyle: ButtonStyle {
    enum Kind {
        case tertiary, secondary, primary
    }

    let kind: Kind

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.system(size: 14))
            .foregroundStyle(kind == .tertiary ? InsureCoPalette.interactive : .white)
            .frame(maxWidth: .infinity, minHeight: 48)
            .background(background)
            .overlay {
                if kind == .tertiary {
                    Rectangle().stroke(InsureCoPalette.interactive, lineWidth: 1)
                }
            }
            .opacity(configuration.isPressed ? 0.7 : 1)
    }

    private var background: Color {
        switch kind {
        case .tertiary: return .clear
        case .secondary: return InsureCoPalette.secondaryButton
        case .primary: return InsureCoPalette.red
        }
    }
}

#Preview("Components") {
    ScrollView {
        VStack(spacing: 24) {
            SignUpHeader()
            StepIndicator(steps: ["Car details", "Driver info", "Coverage", "Review"], currentStep: 0)
            WarningBanner(message: "This is a warning message", onDismiss: {})
            FormField(label: "Make") {
                InsureCoTextField(placeholder: "e.g. Toyota, Ford", text: .constant(""))
            }
            FormField(label: "Year") {
                YearSelect(year: .constant(nil), years: [2026, 2025, 2024])
            }
            FormField(label: "Mileage", isRequired: false) {
                NumberStepperField(label: "Mileage", value: .constant(1000))
            }
            HStack(spacing: 8) {
                Button("Cancel") {}.buttonStyle(InsureCoButtonStyle(kind: .tertiary))
                Button("Back") {}.buttonStyle(InsureCoButtonStyle(kind: .secondary))
                Button("Next") {}.buttonStyle(InsureCoButtonStyle(kind: .primary))
            }
        }
        .padding()
    }
    .preferredColorScheme(.dark)
}
