export function updateUser(state, user) {
    state.user = user;
}

export function updatePermissions(state, permissions) {
    state.permissions = permissions;
}

export function addPermission(state, permission) {
    if (!state.permissions.includes(permission)) {
        state.permissions.push(permission);
    }
}

export function removePermission(state, permission) {
    var index = state.permissions.indexOf(permission);
    if (index > -1) {
        state.permissions.splice(index,1)
    }
}

export function updateRedirectPath(state, redirectPath) {
    state.redirectPath = redirectPath;
}
