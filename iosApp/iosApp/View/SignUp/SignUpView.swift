import SwiftUI

struct SignUpView: View {
    var onBack: () -> Void = {}

    private static let steps = ["Car details", "Driver info", "Coverage", "Review"]
    private static let vinLength = 17
    private static let years: [Int] = {
        let currentYear = Calendar.current.component(.year, from: Date())
        return Array((1980...currentYear + 1).reversed())
    }()

    @State private var make = ""
    @State private var model = ""
    @State private var year: Int?
    @State private var mileage = 1000
    @State private var milesPerYear = 1000
    @State private var vin = ""
    @State private var showWarning = true
    @State private var showErrors = false
    @State private var completedSteps = 0
    @State private var showSavedAlert = false

    private var vinInvalid: Bool {
        !vin.isEmpty && vin.count != Self.vinLength
    }

    private var isValid: Bool {
        !make.trimmingCharacters(in: .whitespaces).isEmpty
            && !model.trimmingCharacters(in: .whitespaces).isEmpty
            && year != nil
            && !vinInvalid
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                SignUpHeader()
                StepIndicator(steps: Self.steps, currentStep: completedSteps)
                if showWarning {
                    WarningBanner(message: "Have your vehicle registration handy before you start.") {
                        withAnimation { showWarning = false }
                    }
                    .transition(.opacity)
                }
                carDetailsCard
            }
            .padding(16)
            .frame(maxWidth: 703)
            .frame(maxWidth: .infinity)
        }
        .scrollDismissesKeyboard(.interactively)
        .navigationTitle("Sign Up")
        .navigationBarTitleDisplayMode(.large)
        .alert("Car details saved", isPresented: $showSavedAlert) {
            Button("OK", role: .cancel) {}
        } message: {
            Text("Next up: tell us about the drivers.")
        }
    }

    private var carDetailsCard: some View {
        VStack(alignment: .leading, spacing: 24) {
            VStack(alignment: .leading, spacing: 12) {
                Text("Car Details")
                    .font(.system(size: 24))
                    .foregroundStyle(InsureCoPalette.textPrimary)
                Rectangle()
                    .fill(InsureCoPalette.red.opacity(0.6))
                    .frame(height: 1)
            }

            Text("Tell us about your car")
                .font(.system(size: 14))
                .foregroundStyle(InsureCoPalette.textPrimary)

            FormField(label: "Make", errorText: makeError) {
                InsureCoTextField(placeholder: "e.g. Toyota, Ford", text: $make, isError: makeError != nil)
                    .autocorrectionDisabled()
            }

            FormField(label: "Model", errorText: modelError) {
                InsureCoTextField(placeholder: "e.g. Corolla, Bronco", text: $model, isError: modelError != nil)
                    .autocorrectionDisabled()
            }

            FormField(label: "Year", errorText: yearError) {
                YearSelect(year: $year, years: Self.years, isError: yearError != nil)
            }

            FormField(label: "Mileage", isRequired: false) {
                NumberStepperField(label: "Mileage", value: $mileage)
            }

            FormField(label: "Miles driven per year", isRequired: false) {
                NumberStepperField(label: "Miles driven per year", value: $milesPerYear)
            }

            FormField(
                label: "VIN (optional)",
                helperText: "\(Self.vinLength) digits",
                errorText: vinInvalid ? "VIN must be exactly \(Self.vinLength) characters" : nil
            ) {
                InsureCoTextField(placeholder: "", text: $vin, isError: vinInvalid)
                    .textInputAutocapitalization(.characters)
                    .autocorrectionDisabled()
                    .onChange(of: vin) { newValue in
                        let sanitized = String(newValue.uppercased().filter { $0.isLetter || $0.isNumber }.prefix(Self.vinLength))
                        if sanitized != newValue { vin = sanitized }
                    }
            }

            HStack(spacing: 8) {
                Button("Cancel", action: reset)
                    .buttonStyle(InsureCoButtonStyle(kind: .tertiary))
                Button(action: onBack) {
                    Label("Back", systemImage: "arrow.left")
                        .labelStyle(TrailingIconLabelStyle())
                }
                .buttonStyle(InsureCoButtonStyle(kind: .secondary))
                Button(action: next) {
                    Label("Next", systemImage: "arrow.right")
                        .labelStyle(TrailingIconLabelStyle())
                }
                .buttonStyle(InsureCoButtonStyle(kind: .primary))
            }
            .padding(.top, 8)
        }
        .padding(24)
        .background(InsureCoPalette.layer)
        .overlay(alignment: .top) {
            Rectangle()
                .fill(InsureCoPalette.red)
                .frame(height: 4)
        }
    }

    private var makeError: String? {
        showErrors && make.trimmingCharacters(in: .whitespaces).isEmpty ? "Enter the make of your car" : nil
    }

    private var modelError: String? {
        showErrors && model.trimmingCharacters(in: .whitespaces).isEmpty ? "Enter the model of your car" : nil
    }

    private var yearError: String? {
        showErrors && year == nil ? "Select a year" : nil
    }

    private func reset() {
        make = ""
        model = ""
        year = nil
        mileage = 1000
        milesPerYear = 1000
        vin = ""
        showErrors = false
        completedSteps = 0
    }

    private func next() {
        showErrors = true
        guard isValid else { return }
        completedSteps = 1
        showSavedAlert = true
    }
}

private struct TrailingIconLabelStyle: LabelStyle {
    func makeBody(configuration: Configuration) -> some View {
        HStack(spacing: 8) {
            configuration.title
            configuration.icon
        }
    }
}

#Preview {
    NavigationStack {
        SignUpView()
    }
    .preferredColorScheme(.dark)
}
