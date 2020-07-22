/*
 * <<
 *  Davinci
 *  ==
 *  Copyright (C) 2016 - 2020 EDP
 *  ==
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  >>
 *
 */

package edp.davinci.server.dao;

import edp.davinci.core.dao.RelRoleDisplayMapper;
import edp.davinci.core.dao.entity.RelRoleDisplay;
import edp.davinci.server.model.RoleDisableViz;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RelRoleDisplayExtendMapper extends RelRoleDisplayMapper {

	@Insert({
		"<script>",
		"	insert ignore rel_role_display" + 
		"		<trim prefix='(' suffix=')' suffixOverrides=','>" + 
		"			`role_id`," + 
		"			`display_id`," + 
		"			`visible`," + 
		"			`create_by`," + 
		"			`create_time`" + 
		"		</trim>" + 
		"		<trim prefix='values (' suffix=')' suffixOverrides=','>" + 
		"			#{roleId,jdbcType=BIGINT}," + 
		"			#{displayId,jdbcType=BIGINT}," + 
		"			#{visible,jdbcType=TINYINT}," + 
		"			#{createBy,jdbcType=BIGINT}," + 
		"			#{createTime,jdbcType=TIMESTAMP}" + 
		"		</trim>",
		"</script>"
	})
	int insert(RelRoleDisplay record);

	@Insert({
		"<script>",
		"	replace into rel_role_display" + 
		"		(`role_id`, `display_id`, `visible`, `create_by`, `create_time`)" + 
		"		values" + 
		"		<foreach collection='list' item='record' index='index' separator=','>" + 
		"		(" + 
		"			#{record.roleId,jdbcType=BIGINT}," + 
		"			#{record.displayId,jdbcType=BIGINT}," + 
		"			#{record.visible,jdbcType=TINYINT}," + 
		"			#{record.createBy,jdbcType=BIGINT}," + 
		"			#{record.createTime,jdbcType=TIMESTAMP}" + 
		"		)" + 
		"		</foreach>",
		"</script>"
	})
	int insertBatch(List<RelRoleDisplay> list);

    @Delete({
            "delete from rel_role_display where display_id = #{id}"
    })
    int deleteByDisplayId(Long id);

    @Select({
            "select rru.role_id as roleId, rrd.display_id as vizId",
            "from rel_role_display rrd",
            "       inner join rel_role_user rru on rru.role_id = rrd.role_id",
            "       inner join display d on d.id = rrd.display_id",
            "where rru.user_id = #{userId} and rrd.visible = 0 and d.project_id = #{projectId}"
    })
    List<RoleDisableViz> getDisableDisplayByUser(@Param("userId") Long userId, @Param("projectId") Long projectId);

    @Select({
            "select role_id from rel_role_display where display_id = #{display_id} and visible = 0"
    })
    List<Long> getByDisplayId(Long displayId);

    @Select({
            "select rrd.display_id",
            "from rel_role_display rrd",
            "inner join display d on d.id = rrd.display_id",
            "where rrd.role_id = #{roleId} and rrd.visible = 0 and d.project_id = #{projectId}"
    })
    List<Long> getExcludeDisplays(@Param("roleId") Long roleId, @Param("projectId") Long projectId);

    @Delete({"delete from rel_role_display where role_id = #{roleId}"})
    int deleteByRoleId(Long roleId);

    @Insert({
            "insert rel_role_display (role_id, display_id, visible, create_by, create_time)",
            "select role_id, ${copyDisplayId}, visible, ${userId}, now() from rel_role_display where display_id = #{originDisplayId}"
    })
    int copyRoleRelation(@Param("originDisplayId") Long originDisplayId, @Param("copyDisplayId") Long copyDisplayId, @Param("userId") Long userId);

    @Delete({"delete from rel_role_display where display_id in (select id from display where project_id = #{projectId})"})
    int deleteByProjectId(Long projectId);
}