export class Post{
    constructor(id, userId,title, content, createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    getId() {
        return this.id;
    }

    getUserId() {
        return this.userId;
    }

    getContent() {
        return this.content;
    }

    getCreatedAt() {
        return this.createdAt;
    }

    getUpdatedAt() {
        return this.updatedAt;
    }

    setTitle(title) {
        this.title = title;
    }

    getTitle() {
        return this.title;
    }
    setContent(content) {
        this.content = content;
    }

    toString() {
        return `Post by User ${this.userId}: ${this.content}`;
    }
    
    static fromAPI(response){
        return new Post(
            response.id,
            response.userId,
            response.title,
            response.content,
            response.createdAt
        )
    }

}