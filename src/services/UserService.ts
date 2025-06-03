import { User } from '../class/User.js';
import { env } from '../constants/env.js';

export class UserService {
    constructor() {
        this.users = [];
        this.currentUser = null;
    }

    async fetchUsers() {
        const response = await fetch(`${env.API_URL}/users`);
        if (!response.ok) {
            throw new Error('Failed to fetch users');
        }
        this.users = await response.json();
        return this.users;
    }

    async createUser(userData) {
        const response = await fetch(`${env.API_URL}/users`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData),
        });
        if (!response.ok) {
            throw new Error('Failed to create user');
        }
        const newUser = await response.json();
        this.users.push(newUser);
        return newUser;
    }
    async fetchCurrentUser() {
        const response = await fetch(`${env.API_URL}/users/me`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        if (!response.ok) {
            throw new Error('Failed to fetch current user');
        }
        this.currentUser = await response.json();
        return User.fromAPI(this.currentUser);
    }
}