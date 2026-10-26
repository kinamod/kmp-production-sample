import SwiftUI
import WebKit

struct SavedPost: Codable, Identifiable, Hashable {
    let title: String
    let link: String
    var id: String { link }
}

final class SavedPostsStore: ObservableObject {
    private static let key = "savedPosts"

    @Published private(set) var posts: [SavedPost]

    init() {
        if let data = UserDefaults.standard.data(forKey: Self.key),
           let decoded = try? JSONDecoder().decode([SavedPost].self, from: data) {
            posts = decoded
        } else {
            posts = []
        }
    }

    func isSaved(_ post: SavedPost) -> Bool {
        posts.contains { $0.link == post.link }
    }

    func toggle(_ post: SavedPost) {
        if let index = posts.firstIndex(where: { $0.link == post.link }) {
            posts.remove(at: index)
        } else {
            posts.insert(post, at: 0)
        }
        persist()
    }

    func remove(at offsets: IndexSet) {
        posts.remove(atOffsets: offsets)
        persist()
    }

    private func persist() {
        if let data = try? JSONEncoder().encode(posts) {
            UserDefaults.standard.set(data, forKey: Self.key)
        }
    }
}

struct WebView: UIViewRepresentable {
    let url: URL

    func makeUIView(context: Context) -> WKWebView {
        let webView = WKWebView()
        webView.load(URLRequest(url: url))
        return webView
    }

    func updateUIView(_ uiView: WKWebView, context: Context) {}
}

struct PostDetailView: View {
    @EnvironmentObject var saved: SavedPostsStore
    let post: SavedPost

    var body: some View {
        if let url = URL(string: post.link) {
            WebView(url: url)
                .navigationTitle(post.title)
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItemGroup(placement: .bottomBar) {
                        Button {
                            saved.toggle(post)
                        } label: {
                            Label(saved.isSaved(post) ? "Saved" : "Save",
                                  systemImage: saved.isSaved(post) ? "bookmark.fill" : "bookmark")
                        }
                        Spacer()
                        ShareLink(item: url)
                    }
                }
        }
    }
}

struct SavedPostsView: View {
    @EnvironmentObject var saved: SavedPostsStore

    var body: some View {
        Group {
            if saved.posts.isEmpty {
                Text("No saved posts yet")
                    .foregroundColor(.secondary)
            } else {
                List {
                    ForEach(saved.posts) { post in
                        NavigationLink(destination: PostDetailView(post: post)) {
                            Text(post.title)
                        }
                    }
                    .onDelete(perform: saved.remove)
                }
            }
        }
        .navigationTitle("Saved")
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    NavigationStack {
        SavedPostsView()
    }
    .environmentObject(SavedPostsStore())
}
