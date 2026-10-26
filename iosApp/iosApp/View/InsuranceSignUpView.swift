import SwiftUI

private enum InsuranceType: CaseIterable, Identifiable {
    case car, home, both

    var id: Self { self }

    var title: String {
        switch self {
        case .car: return "Car Insurance"
        case .home: return "Home Insurance"
        case .both: return "Both Home and Car"
        }
    }

    var subtitle: String {
        switch self {
        case .car: return "Get comprehensive coverage for your vehicle"
        case .home: return "Protect your most important asset for your family"
        case .both: return "Insure both and get bundle savings"
        }
    }

    var symbols: [String] {
        switch self {
        case .car: return ["car"]
        case .home: return ["house"]
        case .both: return ["car", "house"]
        }
    }
}

struct InsuranceSignUpView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var selected: InsuranceType?

    private let red = Color(red: 218 / 255, green: 30 / 255, blue: 40 / 255)
    private let darkRed = Color(red: 158 / 255, green: 27 / 255, blue: 34 / 255)
    private let blue = Color(red: 15 / 255, green: 98 / 255, blue: 254 / 255)
    private let lightGray = Color(red: 244 / 255, green: 244 / 255, blue: 244 / 255)
    private let charcoal = Color(red: 57 / 255, green: 57 / 255, blue: 57 / 255)

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                header
                stepProgress
                formCard
            }
            .padding(16)
        }
        .navigationTitle("InsureCo Sign Up")
        .navigationBarTitleDisplayMode(.inline)
    }

    private var header: some View {
        VStack(spacing: 12) {
            Text("Sign Up for InsureCo")
                .font(.system(size: 28))
            Text("Get started with your insurance coverage in just a few steps")
                .font(.system(size: 16))
        }
        .multilineTextAlignment(.center)
        .foregroundColor(.white)
        .frame(maxWidth: .infinity)
        .padding(.vertical, 28)
        .padding(.horizontal, 16)
        .background(
            LinearGradient(colors: [red, darkRed], startPoint: .topLeading, endPoint: .bottomTrailing)
        )
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }

    private var stepProgress: some View {
        HStack(alignment: .top, spacing: 8) {
            ForEach(0..<4, id: \.self) { index in
                VStack(alignment: .leading, spacing: 12) {
                    Rectangle()
                        .fill(index == 0 ? blue : Color(white: 0.88))
                        .frame(height: 2)
                    HStack(spacing: 8) {
                        Circle()
                            .strokeBorder(index == 0 ? blue : charcoal, lineWidth: 1)
                            .background(Circle().fill(index == 0 ? blue : Color.clear))
                            .frame(width: 14, height: 14)
                        VStack(alignment: .leading, spacing: 2) {
                            Text("Step").font(.system(size: 14)).foregroundColor(.black)
                            Text("Optional label").font(.system(size: 12)).foregroundColor(charcoal)
                        }
                    }
                }
            }
        }
        .padding(16)
        .background(lightGray)
    }

    private var formCard: some View {
        VStack(alignment: .leading, spacing: 24) {
            VStack(alignment: .leading, spacing: 8) {
                Text("What Will You Insure")
                    .font(.system(size: 24))
                    .foregroundColor(.black)
                Rectangle().fill(red).frame(height: 1)
            }
            Text("Which insurance coverage are you looking for")
                .font(.system(size: 14))
                .foregroundColor(.black)
            ForEach(InsuranceType.allCases) { type in
                tile(for: type)
            }
            Rectangle().fill(red).frame(height: 1)
            HStack(spacing: 16) {
                Spacer()
                formButton("Back", systemImage: "arrow.left", color: charcoal) { dismiss() }
                formButton("Next", systemImage: "arrow.right", color: red) {}
                    .disabled(selected == nil)
                    .opacity(selected == nil ? 0.4 : 1)
            }
        }
        .padding(24)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(lightGray)
    }

    private func tile(for type: InsuranceType) -> some View {
        Button {
            selected = type
        } label: {
            HStack(spacing: 16) {
                HStack(spacing: 8) {
                    ForEach(type.symbols, id: \.self) { symbol in
                        Image(systemName: symbol).font(.system(size: 24))
                    }
                }
                VStack(alignment: .leading, spacing: 8) {
                    Text(type.title).font(.system(size: 20, weight: .semibold))
                    Text(type.subtitle).font(.system(size: 14))
                }
                .multilineTextAlignment(.leading)
                Spacer(minLength: 0)
            }
            .foregroundColor(.black)
            .padding(.horizontal, 16)
            .padding(.vertical, 20)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color.white)
            .overlay(
                Rectangle().stroke(selected == type ? red : Color.clear, lineWidth: 2)
            )
        }
        .buttonStyle(.plain)
    }

    private func formButton(_ title: String, systemImage: String, color: Color, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            HStack(spacing: 16) {
                Text(title)
                Image(systemName: systemImage)
            }
            .font(.system(size: 14))
            .foregroundColor(.white)
            .padding(.horizontal, 16)
            .frame(height: 48)
            .background(color)
        }
    }
}

#Preview {
    NavigationStack {
        InsuranceSignUpView()
    }
}
