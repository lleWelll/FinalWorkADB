package org.tech.finalprojectadb.util;

import org.tech.finalprojectadb.entity.UserAction;

import java.util.List;

public record UserFullInfo(String username, List<UserAction> userActivity) {}
