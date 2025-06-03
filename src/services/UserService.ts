import { User } from '../class/User.js';
import { env } from '../constants/env.js';
import { AuthService } from './AuthService.js';
export class UserService {
    static currentUser: User | null;
    static users: User[];
    constructor() {}

    static async fetchUsers() {
        const response = await fetch(`${env.API_URL}/users`);
        if (!response.ok) {
            throw new Error('Failed to fetch users');
        }
        UserService.users = await response.json();
        return UserService.users;
    }

    static async fetchCurrentUser() {
        const response = await fetch(`${env.API_URL}/users/me`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        if (!response.ok) {
            throw new Error('Failed to fetch current user');
        }
        UserService.currentUser = await response.json();
        AuthService.refreshToken();
        return User.fromAPI(UserService.currentUser);
    }

    static async followUser(userToFollowId: number) {
        const currentUser = UserService.currentUser;
        if (!currentUser) {
            throw new Error('Current user is not authenticated');
        }else if (currentUser.id === userToFollowId) {
            throw new Error('You cannot follow yourself');
        }
        const response = await fetch(`${env.API_URL}/users/${userToFollowId}/follow`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify({
                currentUserId: currentUser?.id
            })
        });
        if (!response.ok) {
            throw new Error('Failed to follow user');
        }
    }

    static async unfollowUser(userToUnfollowId: number) {
        const currentUser = UserService.currentUser;
        if (!currentUser) {
            throw new Error('Current user is not authenticated');
        } else if (currentUser.id === userToUnfollowId) {
            throw new Error('You cannot unfollow yourself');
        }
        const response = await fetch(`${env.API_URL}/users/${userToUnfollowId}/unfollow`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify({
                currentUserId: currentUser?.id
            })
        });
        if (!response.ok) {
            throw new Error('Failed to unfollow user');
        }
    }

    static async fetchRecommendedUsers() {
        const response = await fetch(`${env.API_URL}/users/recommended`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        if (!response.ok) {
            throw new Error('Failed to fetch recommended users');
        }
        const data = await response.json();
        return data.map((user: any) => User.fromAPI(user));
    }

    static async fetchUserById(userId: number) {
        const response = await fetch(`${env.API_URL}/users/${userId}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        if (!response.ok) {
            throw new Error('Failed to fetch user');
        }
        const data = await response.json();
        return User.fromAPI(data);
    }

    static async fetchFollowers(userId: number) {
        const response = await fetch(`${env.API_URL}/users/${userId}/followers`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        if (!response.ok) {
            throw new Error('Failed to fetch followers');
        }
        const data = await response.json();
        return data.map((user: any) => User.fromAPI(user));
    }

    static async fetchFollowing(userId: number) {
        const response = await fetch(`${env.API_URL}/users/${userId}/following`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        if (!response.ok) {
            throw new Error('Failed to fetch following');
        }
        const data = await response.json();
        return data.map((user: any) => User.fromAPI(user));
    }
}