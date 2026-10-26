//
//  RssRow.swift
//  iosApp
//
//  Created by Ekaterina.Petrova on 25.10.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI
import RssReader
import URLImage
import Foundation

struct PostRow: View {
    let post: Item

    var body: some View {
        if let postURL = post.link, URL(string: postURL) != nil {
            NavigationLink(destination: PostDetailView(post: SavedPost(title: (post.title ?? postURL).decodeHtmlEntities(), link: postURL))) {
                content
            }
            .foregroundColor(.primary)
        } else {
            content
        }
    }

    var content: some View {
        VStack(alignment: .leading, spacing: 10.0) {
            if let title = post.title {
                Text(title.decodeHtmlEntities()).bold().font(.title3)
            }
            if let imageUrl = post.getImageUrl(), let url = URL(string: imageUrl) {
                URLImage(url: url) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                }
                .frame(minWidth: 0, maxWidth: .infinity)
                .clipped()
            }
            if let descr = post.description_ {
                Text(descr.decodeHtmlEntities()).font(.body).lineLimit(15)
            }
            HStack{
                Spacer()
                if let dateString = post.pubDate {
                    Text(dateString).font(.footnote).foregroundColor(.gray)
                }
            }
        }
    }
}

extension Item {
    static let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "E, MMM d HH:mm"
        return formatter
    }()

    var linkURL: URL? {
        if let link = link {
            return URL(string: link)
        } else {
            return nil
        }
    }
}
