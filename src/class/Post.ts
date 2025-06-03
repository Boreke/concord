export class Post{
    id: number;
    userId: number;
    title: string;
    content: string;
    createdAt: string;
    likeCount: number;
    constructor(id: number, userId: number, title: string, content: string, createdAt: string, likeCount: number) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.likeCount = likeCount || 0;
    }

    toString(): string {
        return `Post by User ${this.userId}: ${this.content}`;
    }

    static fromAPI(response: any): Post {
        return new Post(
            response.id,
            response.userId,
            response.title,
            response.content,
            response.createdAt,
            response.likeCount || 0 // Default to 0 if likeCount is not provided
        )
    }

}