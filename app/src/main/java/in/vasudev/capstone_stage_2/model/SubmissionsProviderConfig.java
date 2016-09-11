package in.vasudev.capstone_stage_2.model;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;

/**
 * Created by vineet on 11-Sep-16.
 */
@SimpleSQLConfig(
        name = "SubmissionsProvider",
        authority = "in.vasudev.capstone_stage_2.submissions_provider.authority",
        database = "database.db",
        version = 1)
public class SubmissionsProviderConfig implements ProviderConfig {
    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }
}