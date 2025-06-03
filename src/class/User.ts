export class User {
    id: number;
    userTag: string;
    email: string;
    displayName: string;
    role: string;
    _isPrivate: boolean;

    constructor(id: number, userTag: string, email: string, displayName: string, role: string, isPrivate = false) {
        this.id = id;
        this.userTag = userTag;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
        this._isPrivate = isPrivate;
    }
    get isPrivate(): boolean {
        return this._isPrivate;
    }
    toString(): string {
        return `User: ${this.userTag} (${this.displayName})`;
    }
    static fromAPI(response: any): User {
        return new User(
            response.id,
            response.userTag,
            response.email,
            response.displayName,
            response.role,
            response.isPrivate
        );
    }
}