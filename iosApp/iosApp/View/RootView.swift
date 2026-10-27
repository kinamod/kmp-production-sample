import SwiftUI
import RssReader

struct RootView: View {
    enum AppTab: Hashable {
        case feed, signUp
    }

    @EnvironmentObject var store: ObservableFeedStore
    @SwiftUI.State var errorMessage: String?
    @SwiftUI.State private var selectedTab: AppTab = .feed

    var body: some View {
        ZStack {
            TabView(selection: $selectedTab) {
                NavigationStack {
                    MainFeedView()
                }
                .tabItem { Label("Feed", systemImage: "newspaper") }
                .tag(AppTab.feed)

                NavigationStack {
                    SignUpView(onBack: { selectedTab = .feed })
                }
                .tabItem { Label("Sign Up", systemImage: "person.crop.circle.badge.plus") }
                .tag(AppTab.signUp)
            }
            .zIndex(0)
            if let errorMessage = self.errorMessage {
                VStack {
                    Spacer()
                    Text(errorMessage)
                        .foregroundColor(.white)
                        .padding(10.0)
                        .background(Color.black)
                        .cornerRadius(3.0)
                }
                .padding(.bottom, 64)
                .zIndex(1)
                .transition(.asymmetric(insertion: .move(edge: .bottom), removal: .opacity) )
            }
        }
        .onReceive(store.$sideEffect) { value in
            if let errorMessage = (value as? FeedSideEffect.Error)?.error.message {
                withAnimation { self.errorMessage = errorMessage }
                DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                    withAnimation { self.errorMessage = nil }
                }
            }
        }
    }
}
