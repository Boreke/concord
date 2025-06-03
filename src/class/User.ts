export class User {
    id: number;
    userTag: string;
    email: string;
    displayName: string;
    role: string;
    createdAt: string;
    _isPrivate: boolean;

    constructor(id: number, userTag: string, email: string, displayName: string, role: string, createdAt: string, isPrivate = false) {
        this.id = id;
        this.userTag = userTag;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
        this.createdAt = createdAt;
        this._isPrivate = isPrivate;
    }

    getId(): number {
        return this.id;
    }

    getEmail(): string {
        return this.email;
    }

    setEmail(email: string) {
        this.email = email;
    }
    setRole(role: string) {
        this.role = role;
    }
    setPrivate(isPrivate: boolean) {
        this._isPrivate = isPrivate;
    }
    getRole(): string {
        return this.role;
    }
    getDisplayName(): string {
        return this.displayName;
    }
    getUserTag(): string {
        return this.userTag;
    }
    isPrivate(): boolean {
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
            response.createdAt,
            response.isPrivate
        );
    }
}